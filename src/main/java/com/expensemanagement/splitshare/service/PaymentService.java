package com.expensemanagement.splitshare.service;

import com.expensemanagement.splitshare.dao.GroupsDao;
import com.expensemanagement.splitshare.dto.CreateUpdateSplitRequest;
import com.expensemanagement.splitshare.dto.CreateUpdateSplitResponse;
import com.expensemanagement.splitshare.entity.GroupsEntity;
import com.expensemanagement.splitshare.entity.PaymentDetailsEntity;
import com.expensemanagement.splitshare.entity.SplitInformationEntity;
import com.expensemanagement.splitshare.entity.UserSplitDetailsEntity;
import com.expensemanagement.splitshare.entity.UserSplitPaymentsEntity;
import com.expensemanagement.splitshare.enums.PaymentPartyEnum;
import com.expensemanagement.splitshare.enums.PaymentStatusEnum;
import com.expensemanagement.splitshare.exception.InternalServerException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;
import org.apache.commons.lang3.tuple.MutablePair;
import org.apache.commons.lang3.tuple.Pair;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class PaymentService {
    private final GroupsDao groupsDao;

    @Autowired
    public PaymentService(GroupsDao groupsDao) {
        this.groupsDao = groupsDao;
    }


    /**
     * 1. Get the group based on groupId
     * 2. Build a graph from user_split_detail table for the group. This graph implies how much already does each user owe.
     * 3. Build paymentEntity for the current / latest payment
     *  3.1 Calculate how much does one owe other with the current payment and build userSplitPaymentEntity
     *  3.2 Update userSplitTrack for overall track of payments
     * 4. Save the data to group
     * @param createUpdateSplitRequest
     * @return createUpdateSplitResponse
     */
    public CreateUpdateSplitResponse createUpdateSplit(CreateUpdateSplitRequest createUpdateSplitRequest) {
        try {
            // Step 1: Get groupEntity from database
            GroupsEntity group = groupsDao.getGroupByGroupId(createUpdateSplitRequest.getGroupId());
            List<Long> existingPaymentIds = group.getPayments()
                    .stream()
                    .map(PaymentDetailsEntity::getPaymentId)
                    .toList();

            // Step 2: Build PaymentDetailsEntity, UserSplitPaymentsEntity, SplitInformationEntity for add and update operation
            PaymentDetailsEntity paymentDetailsEntity = null;
            Set<UserSplitPaymentsEntity> existingUserSplitPaymentSet = null;
            Set<UserSplitPaymentsEntity> existingActiveUserSplitPaymentSet = null;
            Set<SplitInformationEntity> splitInformationEntitySet = new HashSet<>();
            Set<SplitInformationEntity> existingSplitInformationEntitySet = null;
            Set<SplitInformationEntity> existingActiveSplitInformationEntitySet = null;
            Long version = 0L;
            if (Objects.nonNull(createUpdateSplitRequest.getPaymentId())) {
                paymentDetailsEntity = group.getPayments()
                        .stream()
                        .filter(payment -> payment.getPaymentId().equals(createUpdateSplitRequest.getPaymentId()))
                        .findFirst()
                        .get();
                existingUserSplitPaymentSet = paymentDetailsEntity.getUserSplitPayments();
                existingActiveUserSplitPaymentSet = existingUserSplitPaymentSet
                        .stream()
                        .filter(userSplitPayment -> !userSplitPayment.isOutdated())
                        .collect(Collectors.toSet());
                for (UserSplitPaymentsEntity existingActiveUserSplitPayment : existingActiveUserSplitPaymentSet) {
                    version = existingActiveUserSplitPayment.getVersion();
                    existingActiveUserSplitPayment.setOutdated(true);
                }
                existingSplitInformationEntitySet = paymentDetailsEntity.getSplit();
                existingActiveSplitInformationEntitySet = existingSplitInformationEntitySet
                        .stream()
                        .filter(splitInformation -> !splitInformation.isOutdated())
                        .collect(Collectors.toSet());
                for (SplitInformationEntity existingActiveSplitInformationEntity : existingActiveSplitInformationEntitySet) {
                    existingActiveSplitInformationEntity.setOutdated(true);
                }
            } else {
                paymentDetailsEntity = new PaymentDetailsEntity();
            }
            paymentDetailsEntity.setAmount(createUpdateSplitRequest.getTotalAmount());
            paymentDetailsEntity.setSplitMethod(createUpdateSplitRequest.getSplitMethod());

            // PAYER
            List<SplitInformationEntity> payerSplitList = new ArrayList<>();
            for (Long payerUserId : createUpdateSplitRequest.getPayerSplitMap().keySet()) {
                SplitInformationEntity splitInformationEntity = new SplitInformationEntity();
                splitInformationEntity.setUserId(payerUserId);
                splitInformationEntity.setPaymentParty(PaymentPartyEnum.PAYER.toString());
                Double contribution = createUpdateSplitRequest.getPayerSplitMap().get(payerUserId);
                Double fraction = contribution / createUpdateSplitRequest.getTotalAmount();
                splitInformationEntity.setSplitFraction(fraction);
                splitInformationEntity.setAmount(contribution);
                splitInformationEntity.setVersion(version+1);
                splitInformationEntity.setPaymentDetail(paymentDetailsEntity);
                payerSplitList.add(splitInformationEntity);
                splitInformationEntitySet.add(splitInformationEntity);
            }
            // DEBTOR
            List<SplitInformationEntity> debtorSplitList = new ArrayList<>();
            for (Long debtorUserId : createUpdateSplitRequest.getDebtorSplitMap().keySet()) {
                SplitInformationEntity splitInformationEntity = new SplitInformationEntity();
                splitInformationEntity.setUserId(debtorUserId);
                splitInformationEntity.setPaymentParty(PaymentPartyEnum.DEBTOR.toString());
                Double contribution = createUpdateSplitRequest.getDebtorSplitMap().get(debtorUserId);
                Double fraction = contribution / createUpdateSplitRequest.getTotalAmount();
                splitInformationEntity.setSplitFraction(fraction);
                splitInformationEntity.setAmount(contribution);
                splitInformationEntity.setVersion(version+1);
                splitInformationEntity.setPaymentDetail(paymentDetailsEntity);
                splitInformationEntitySet.add(splitInformationEntity);
                debtorSplitList.add(splitInformationEntity);
            }

            // Step 3: Create graph from existing user_split_detail table for the group
            Map<Long, List<Pair<Long, Double>>> userSplitTrack = new HashMap<>();
            for (UserSplitDetailsEntity userSplitDetail : group.getUserSplitDetails()) {
                addUserPaymentsToMap(userSplitTrack, userSplitDetail.getFromUserId(), userSplitDetail.getToUserId(), userSplitDetail.getAmountOwed());
            }

            // Step 4: Calculate how much does one owe other with the current payment
            Map<Long, List<Pair<Long, Double>>> userPaymentsTrack = calculateSplit(userSplitTrack, payerSplitList, debtorSplitList);
            Set<UserSplitPaymentsEntity> userSplitPaymentsEntitySet = buildUserSplitPaymentsEntity(userPaymentsTrack, paymentDetailsEntity, version+1);

            if (Objects.nonNull(existingUserSplitPaymentSet)) {
                userSplitPaymentsEntitySet.addAll(existingUserSplitPaymentSet);
            }
            if (Objects.nonNull(existingSplitInformationEntitySet)) {
                splitInformationEntitySet.addAll(existingActiveSplitInformationEntitySet);
            }
            paymentDetailsEntity.setUserSplitPayments(userSplitPaymentsEntitySet);
            paymentDetailsEntity.setSplit(splitInformationEntitySet);
            group.getPayments().add(paymentDetailsEntity);
            paymentDetailsEntity.setGroup(group);

            // Step 5: For update operation, reduce the previous payment split from user split details.
            // Update existing userSplitDetailsEntitySet with the current payment split
            Set<UserSplitDetailsEntity> userSplitDetailsEntitySet = group.getUserSplitDetails();
            if (Objects.nonNull(existingActiveUserSplitPaymentSet)) {
                for (UserSplitPaymentsEntity existingActiveUserSplitPayment : existingActiveUserSplitPaymentSet) {
                    for (Pair<Long, Double> debtorMoneyTrack : userSplitTrack.get(existingActiveUserSplitPayment.getFromUserId())) {
                        if (existingActiveUserSplitPayment.getToUserId().equals(debtorMoneyTrack.getLeft())) {
                            debtorMoneyTrack.setValue(debtorMoneyTrack.getRight() - existingActiveUserSplitPayment.getAmountOwed());
                        }
                    }
                }
            }
            for (Long debtorUserId : userSplitTrack.keySet()) {
                for (Pair<Long, Double> debtorMoneyTrack : userSplitTrack.get(debtorUserId)) {
                    Optional<UserSplitDetailsEntity> userSplitDetail = userSplitDetailsEntitySet
                            .stream()
                            .filter(userSplitDetailEntity -> userSplitDetailEntity.getFromUserId().equals(debtorUserId)
                                    && userSplitDetailEntity.getToUserId().equals(debtorMoneyTrack.getLeft()))
                            .findAny();
                    if (userSplitDetail.isPresent()) {
                        userSplitDetail.get().setAmountOwed(debtorMoneyTrack.getRight());
                    } else {
                        UserSplitDetailsEntity userSplitDetailEntity = new UserSplitDetailsEntity();
                        userSplitDetailEntity.setFromUserId(debtorUserId);
                        userSplitDetailEntity.setToUserId(debtorMoneyTrack.getLeft());
                        userSplitDetailEntity.setAmountOwed(debtorMoneyTrack.getRight());
                        userSplitDetailEntity.setAmountPaid(0.0);
                        userSplitDetailEntity.setPaymentStatus(PaymentStatusEnum.UNSETTLED.name());
                        userSplitDetailEntity.setGroup(group);
                        userSplitDetailsEntitySet.add(userSplitDetailEntity);
                    }
                }
            }
            group.setUserSplitDetails(userSplitDetailsEntitySet);
            // Step 6: Upsert group entity to database
            GroupsEntity groupSavedToDb = groupsDao.saveToDb(group);
            Long latestPaymentId = Objects.nonNull(createUpdateSplitRequest.getPaymentId())
                                                                ? createUpdateSplitRequest.getPaymentId() : groupSavedToDb.getPayments()
                                                                                .stream()
                                                                                .map(PaymentDetailsEntity::getPaymentId)
                                                                                .filter(paymentId -> !existingPaymentIds.contains(paymentId))
                                                                                .findFirst()
                                                                                .get();
            return buildCreateSplitResponse(groupSavedToDb, latestPaymentId);
        } catch (SQLException ex) {
            throw new InternalServerException(ex.getMessage());
        }
    }

    private Map<Long, List<Pair<Long, Double>>> calculateSplit(Map<Long, List<Pair<Long, Double>>> userSplitTrack, List<SplitInformationEntity> payerSplitList, List<SplitInformationEntity> debtorSplitList) {
        Map<Long, List<Pair<Long, Double>>> userPaymentsTrack = new HashMap<>();
        int payerIndex = 0, debtorIndex = 0;
        Double amountPaidByUser = payerSplitList.get(payerIndex).getAmount();
        Double amountOwedByUser = debtorSplitList.get(debtorIndex).getAmount();
        for (; payerIndex < payerSplitList.size() && debtorIndex < debtorSplitList.size(); ) {
            if (amountOwedByUser <= amountPaidByUser) {
                addUserPaymentsToMap(userSplitTrack, debtorSplitList.get(debtorIndex).getUserId(), payerSplitList.get(payerIndex).getUserId(), amountOwedByUser);
                addUserPaymentsToMap(userPaymentsTrack, debtorSplitList.get(debtorIndex).getUserId(), payerSplitList.get(payerIndex).getUserId(), amountOwedByUser);
                amountPaidByUser -= amountOwedByUser;
                debtorIndex++;
                if (debtorIndex != debtorSplitList.size()) {
                    amountOwedByUser = debtorSplitList.get(debtorIndex).getAmount();
                }
            } else {
                addUserPaymentsToMap(userSplitTrack, debtorSplitList.get(debtorIndex).getUserId(), payerSplitList.get(payerIndex).getUserId(), amountPaidByUser);
                addUserPaymentsToMap(userPaymentsTrack, debtorSplitList.get(debtorIndex).getUserId(), payerSplitList.get(payerIndex).getUserId(), amountOwedByUser);
                payerIndex++;
                if (payerIndex != payerSplitList.size()) {
                    amountPaidByUser = payerSplitList.get(payerIndex).getAmount();
                }
            }
        }
        return userPaymentsTrack;
    }

    private void addUserPaymentsToMap(Map<Long, List<Pair<Long, Double>>> userPaymentsTrack, Long fromUserId, Long toUserId, Double amountOwed) {
        if (userPaymentsTrack.containsKey(fromUserId)) {
            boolean toUserIdPresent = false;
            for (int i = 0; i < userPaymentsTrack.get(fromUserId).size(); i++) {
                if (userPaymentsTrack.get(fromUserId).get(i).getLeft().equals(toUserId)) {
                    Pair<Long, Double> updatedPair = new MutablePair<>(toUserId, userPaymentsTrack.get(fromUserId).get(i).getRight() + amountOwed);
                    userPaymentsTrack.get(fromUserId).set(i, updatedPair);
                    toUserIdPresent = true;
                }
            }
            if (!toUserIdPresent) {
                userPaymentsTrack.get(fromUserId).add(new MutablePair<>(toUserId, amountOwed));
            }
        } else {
            List<Pair<Long, Double>> debtorPaymentDetails = new ArrayList<>();
            debtorPaymentDetails.add(new MutablePair<>(toUserId, amountOwed));
            userPaymentsTrack.put(fromUserId, debtorPaymentDetails);
        }
    }

    private Set<UserSplitPaymentsEntity> buildUserSplitPaymentsEntity(Map<Long, List<Pair<Long, Double>>> userPaymentsTrack, PaymentDetailsEntity paymentDetailsEntity, Long version) {
        Set<UserSplitPaymentsEntity> userSplitPaymentsEntitySet = new HashSet<>();
        for (Map.Entry<Long, List<Pair<Long, Double>>> userPaymentTrack : userPaymentsTrack.entrySet()) {
            for (Pair<Long, Double> debtorTrack : userPaymentTrack.getValue()) {
                UserSplitPaymentsEntity userSplitPaymentsEntity = new UserSplitPaymentsEntity();
                userSplitPaymentsEntity.setFromUserId(userPaymentTrack.getKey());
                userSplitPaymentsEntity.setToUserId(debtorTrack.getLeft());
                userSplitPaymentsEntity.setAmountOwed(debtorTrack.getRight());
                userSplitPaymentsEntity.setOutdated(false);
                userSplitPaymentsEntity.setVersion(version);
                userSplitPaymentsEntity.setPayment(paymentDetailsEntity);
                userSplitPaymentsEntitySet.add(userSplitPaymentsEntity);
            }
        }
        return userSplitPaymentsEntitySet;
    }

    private CreateUpdateSplitResponse buildCreateSplitResponse(GroupsEntity group, Long latestPaymentId) {
        CreateUpdateSplitResponse createUpdateSplitResponse = new CreateUpdateSplitResponse();
        createUpdateSplitResponse.setGroupId(group.getGroupId());
        createUpdateSplitResponse.setGroupName(group.getGroupName());
        Optional<PaymentDetailsEntity> optionalPaymentDetail = group.getPayments().stream().filter(paymentDetailsEntity -> paymentDetailsEntity.getPaymentId().equals(latestPaymentId)).findFirst();
        if (optionalPaymentDetail.isPresent()) {
            PaymentDetailsEntity paymentDetail = optionalPaymentDetail.get();
            Set<UserSplitPaymentsEntity> userSplitPaymentsEntitySet = paymentDetail.getUserSplitPayments();
            Map<Long, List<Pair<Long, Double>>> userPaymentsTrack = new HashMap<>();
            userSplitPaymentsEntitySet.forEach(userSplitPaymentsEntity -> {
                addUserPaymentsToMap(userPaymentsTrack, userSplitPaymentsEntity.getFromUserId(), userSplitPaymentsEntity.getToUserId(), userSplitPaymentsEntity.getAmountOwed());
            });
            createUpdateSplitResponse.setUserPaymentTrack(userPaymentsTrack);
            createUpdateSplitResponse.setPaymentId(paymentDetail.getPaymentId());
            createUpdateSplitResponse.setUpdatePayment(false);
        }
        return createUpdateSplitResponse;
    }

}