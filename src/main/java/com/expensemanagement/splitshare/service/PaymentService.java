package com.expensemanagement.splitshare.service;

import com.expensemanagement.splitshare.dao.GroupsDao;
import com.expensemanagement.splitshare.dao.PaymentsDao;
import com.expensemanagement.splitshare.dto.CreateUpdateSplitRequest;
import com.expensemanagement.splitshare.dto.CreateUpdateSplitResponse;
import com.expensemanagement.splitshare.dto.GetGroupTransactionsResponse;
import com.expensemanagement.splitshare.entity.GroupsEntity;
import com.expensemanagement.splitshare.entity.PaymentDetailsEntity;
import com.expensemanagement.splitshare.entity.SplitInformationEntity;
import com.expensemanagement.splitshare.entity.UserSplitDetailsEntity;
import com.expensemanagement.splitshare.entity.UsersEntity;
import com.expensemanagement.splitshare.enums.CashFlowEnum;
import com.expensemanagement.splitshare.enums.PaymentPartyEnum;
import com.expensemanagement.splitshare.enums.PaymentStatusEnum;
import com.expensemanagement.splitshare.exception.InternalServerException;
import com.expensemanagement.splitshare.model.ShareDetails;
import com.expensemanagement.splitshare.model.UserPaymentInformation;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import org.apache.commons.lang3.tuple.MutablePair;
import org.apache.commons.lang3.tuple.Pair;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class PaymentService {
    private final PaymentsDao paymentsDao;
    private final GroupsDao groupsDao;

    @Autowired
    public PaymentService(PaymentsDao paymentsDao, GroupsDao groupsDao) {
        this.paymentsDao = paymentsDao;
        this.groupsDao = groupsDao;
    }


    /**
     * 1. Iterates over the payers and set details of how much each user has paid
     * 2. Iterates over the debtors and set details of how much each user has to pay
     * @param createUpdateSplitRequest
     * @return
     */
    public void createOrUpdateSplit(CreateUpdateSplitRequest createUpdateSplitRequest) {
        try {
            // Step 1: Get groupEntity from database
            GroupsEntity group = groupsDao.getGroupByGroupId(createUpdateSplitRequest.getGroupId());
            // Step 2: Create a graph from existing user_split_detail table for the group
            Map<Long, List<Pair<Long, Double>>> userPaymentsTrack = new HashMap<>();
            for (UserSplitDetailsEntity userSplitDetail : group.getUserSplitDetails()) {
                Double pendingAmount = userSplitDetail.getAmountOwed() - userSplitDetail.getAmountPaid();
                addUserPaymentsToMap(userPaymentsTrack, userSplitDetail.getFromUserId(), userSplitDetail.getToUserId(), pendingAmount);
            }
            // Step 3: Build PaymentDetailsEntity and set it to group and vice versa
            PaymentDetailsEntity paymentDetailsEntity = new PaymentDetailsEntity();
            if (Objects.nonNull(createUpdateSplitRequest.getPaymentId())) {
                paymentDetailsEntity.setPaymentId(createUpdateSplitRequest.getPaymentId());
            }
            paymentDetailsEntity.setAmount(createUpdateSplitRequest.getTotalAmount());
            paymentDetailsEntity.setSplitMethod(createUpdateSplitRequest.getSplitMethod());
            Set<SplitInformationEntity> splitInformationEntitySet = new HashSet<>();
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
                splitInformationEntity.setPaymentDetail(paymentDetailsEntity);
                splitInformationEntitySet.add(splitInformationEntity);
                debtorSplitList.add(splitInformationEntity);
            }
            paymentDetailsEntity.setSplit(splitInformationEntitySet);

            group.getPayments().add(paymentDetailsEntity);
            paymentDetailsEntity.setGroup(group);
            // Step 4: Calculate how much does one owe other with the current payment
            calculateSplit(userPaymentsTrack, payerSplitList, debtorSplitList);
            // Step 5: Update existing userSplitDetailsEntitySet with the current payment split
            Set<UserSplitDetailsEntity> userSplitDetailsEntitySet = group.getUserSplitDetails();
            for (Long debtorUserId : userPaymentsTrack.keySet()) {
                for (Pair<Long, Double> debtorMoneyTrack : userPaymentsTrack.get(debtorUserId)) {
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
                        userSplitDetailEntity.setGroup(group);
                        userSplitDetailsEntitySet.add(userSplitDetailEntity);
                    }
                }
            }
            // Step 6: Upsert group entity to database
            GroupsEntity groupSavedToDb = groupsDao.saveToDb(group);
        } catch (SQLException ex) {
            throw new InternalServerException(ex.getMessage());
        }
    }

    public GetGroupTransactionsResponse getDetailedGroupTransactions(Long groupId) {
        GetGroupTransactionsResponse getGroupTransactionsResponse = new GetGroupTransactionsResponse();
        GroupsEntity group = groupsDao.getGroupByGroupId(groupId);
        getGroupTransactionsResponse.setGroupId(group.getGroupId());
        getGroupTransactionsResponse.setGroupName(group.getGroupName());
        Map<Long, List<Pair<Long, Double>>> userPaymentsTrack = new HashMap<>();
        for (PaymentDetailsEntity paymentDetail : group.getPayments()) {
            List<SplitInformationEntity> payerSplitList = paymentDetail.getSplit()
                    .stream()
                    .filter(split -> split.getPaymentParty().equalsIgnoreCase(PaymentPartyEnum.PAYER.toString()))
                    .toList();
            List<SplitInformationEntity> debtorSplitList = paymentDetail.getSplit()
                    .stream()
                    .filter(split -> split.getPaymentParty().equalsIgnoreCase(PaymentPartyEnum.DEBTOR.toString()))
                    .toList();

            calculateSplit(userPaymentsTrack, payerSplitList, debtorSplitList);
        }
        Map<Long, List<Pair<Long, Double>>> prunedDebtorUserPaymentsTrack = simplifyUserPaymentsMap(userPaymentsTrack);
        Map<Long, List<Pair<Long, Double>>> prunedPayerUserPaymentsTrack = reverseDebtorUserPaymentsTrack(prunedDebtorUserPaymentsTrack);
        return convertToGroupTransactionsResponse(groupId, group.getGroupName(), group.getUsers(), prunedDebtorUserPaymentsTrack, prunedPayerUserPaymentsTrack);
    }

    private void calculateSplit(Map<Long, List<Pair<Long, Double>>> userPaymentsTrack, List<SplitInformationEntity> payerSplitList, List<SplitInformationEntity> debtorSplitList) {
        int payerIndex = 0, debtorIndex = 0;
        Double amountPaidByUser = payerSplitList.get(payerIndex).getAmount();
        Double amountOwedByUser = debtorSplitList.get(debtorIndex).getAmount();
        for (; payerIndex < payerSplitList.size() && debtorIndex < debtorSplitList.size(); ) {
            if (amountOwedByUser <= amountPaidByUser) {
                addUserPaymentsToMap(userPaymentsTrack, debtorSplitList.get(debtorIndex).getUserId(), payerSplitList.get(payerIndex).getUserId(), amountOwedByUser);
                amountPaidByUser -= amountOwedByUser;
                debtorIndex++;
                if (debtorIndex != debtorSplitList.size()) {
                    amountOwedByUser = debtorSplitList.get(debtorIndex).getAmount();
                }
            } else {
                addUserPaymentsToMap(userPaymentsTrack, debtorSplitList.get(debtorIndex).getUserId(), payerSplitList.get(payerIndex).getUserId(), amountPaidByUser);
                payerIndex++;
                if (payerIndex != payerSplitList.size()) {
                    amountPaidByUser = payerSplitList.get(payerIndex).getAmount();
                }
            }
        }
    }

    private void addUserPaymentsToMap(Map<Long, List<Pair<Long, Double>>> userPaymentsTrack, Long fromUserId, Long toUserId, Double amountOwed) {
        if (userPaymentsTrack.containsKey(fromUserId)) {
            for (int i = 0; i < userPaymentsTrack.get(fromUserId).size(); i++) {
                if (userPaymentsTrack.get(fromUserId).get(i).getLeft().equals(toUserId)) {
                    Pair<Long, Double> updatedPair = new MutablePair<>(toUserId, userPaymentsTrack.get(fromUserId).get(i).getRight() + amountOwed);
                    userPaymentsTrack.get(fromUserId).set(i, updatedPair);
                }
            }
        } else {
            List<Pair<Long, Double>> debtorPaymentDetails = new ArrayList<>();
            debtorPaymentDetails.add(new MutablePair<>(toUserId, amountOwed));
            userPaymentsTrack.put(fromUserId, debtorPaymentDetails);
        }
    }

    private Map<Long, List<Pair<Long, Double>>> simplifyUserPaymentsMap(Map<Long, List<Pair<Long, Double>>> userPaymentsTrack) {
        Map<Long, List<Pair<Long, Double>>> prunedUserPaymentsTrack = new HashMap<>();
        for (Map.Entry<Long, List<Pair<Long, Double>>> debtorPaymentTrack : userPaymentsTrack.entrySet()) {
            Long debtorUserId = debtorPaymentTrack.getKey();
            List<Pair<Long, Double>> simplifiedDebtorPayments = new ArrayList<>();
            List<Pair<Long, Double>> debtorPayments = debtorPaymentTrack.getValue();
            debtorPayments.sort(Comparator.comparing(Pair::getKey));
            Double amountForSameUser = debtorPayments.getFirst().getValue();
            for (int i = 0; i < debtorPayments.size()-1; i++) {
                if (debtorPayments.get(i).getKey().equals(debtorPayments.get(i+1).getKey())) {
                    amountForSameUser += debtorPayments.get(i+1).getValue();
                } else {
                    simplifiedDebtorPayments.add(new MutablePair<>(debtorPayments.get(i).getKey(), amountForSameUser));
                    amountForSameUser = debtorPayments.get(i+1).getValue();
                }
            }
            simplifiedDebtorPayments.add(new MutablePair<>(debtorPayments.getLast().getKey(), amountForSameUser));
            prunedUserPaymentsTrack.put(debtorUserId, simplifiedDebtorPayments);
        }
        return prunedUserPaymentsTrack;
    }

    private Map<Long, List<Pair<Long, Double>>> reverseDebtorUserPaymentsTrack(Map<Long, List<Pair<Long, Double>>> prunedDebtorUserPaymentsTrack) {
        Map<Long, List<Pair<Long, Double>>> reverseGraph = new HashMap<>();
        for (Map.Entry<Long, List<Pair<Long, Double>>> debtorPaymentTrack : prunedDebtorUserPaymentsTrack.entrySet()) {
            for (Pair<Long, Double> debtorPayment : debtorPaymentTrack.getValue()) {
                addUserPaymentsToMap(reverseGraph, debtorPayment.getLeft(), debtorPaymentTrack.getKey(), debtorPayment.getRight());
            }
        }
        return reverseGraph;
    }

    private GetGroupTransactionsResponse convertToGroupTransactionsResponse(Long groupId, String groupName, Set<UsersEntity> users,
                                                                            Map<Long, List<Pair<Long, Double>>> prunedDebtorUserPaymentsTrack,
                                                                            Map<Long, List<Pair<Long, Double>>> prunedPayerUserPaymentsTrack) {
        GetGroupTransactionsResponse getGroupTransactionsResponse = new GetGroupTransactionsResponse();
        getGroupTransactionsResponse.setGroupId(groupId);
        getGroupTransactionsResponse.setGroupName(groupName);
        for (UsersEntity user : users) {
            UserPaymentInformation userPaymentInformation = new UserPaymentInformation();
            userPaymentInformation.setUserId(user.getUserId());
            userPaymentInformation.setUserName(user.getUserName());
            Double totalOutflowFromUser = calculateSum(prunedDebtorUserPaymentsTrack, user.getUserId());
            Double totalInflowToUser = calculateSum(prunedPayerUserPaymentsTrack, user.getUserId());
            userPaymentInformation.setTotalPendingOutflow(totalOutflowFromUser);
            userPaymentInformation.setTotalPendingInflow(totalInflowToUser);
            List<ShareDetails> shareDetailsList = new ArrayList<>();
            shareDetailsList.addAll(buildShareDetails(prunedDebtorUserPaymentsTrack.get(user.getUserId()), CashFlowEnum.OUTFLOW));
            shareDetailsList.addAll(buildShareDetails(prunedPayerUserPaymentsTrack.get(user.getUserId()), CashFlowEnum.INFLOW));
            userPaymentInformation.setShareDetailsList(shareDetailsList);
        }
        return getGroupTransactionsResponse;
    }

    private Double calculateSum(Map<Long, List<Pair<Long, Double>>> paymentsTrack, Long userId) {
        Double pendingMoney = null;
        if (paymentsTrack.containsKey(userId)) {
            pendingMoney = paymentsTrack.get(userId)
                    .stream()
                    .filter(Objects::nonNull)
                    .map(Pair::getValue)
                    .reduce(0.0, Double::sum);
        }
        return pendingMoney;
    }

    private List<ShareDetails> buildShareDetails(List<Pair<Long, Double>> paymentsTrack, CashFlowEnum cashFlowEnum) {
        List<ShareDetails> shareDetailsList = new ArrayList<>();
        if (Objects.nonNull(paymentsTrack)) {
            for (Pair<Long, Double> paymentTrack : paymentsTrack) {
                ShareDetails shareDetail = new ShareDetails();
                shareDetail.setUserId(paymentTrack.getLeft());
                shareDetail.setAmount(paymentTrack.getRight());
                shareDetail.setRelation(cashFlowEnum.toString());
                shareDetailsList.add(shareDetail);
            }
        }
        return shareDetailsList;
    }
}