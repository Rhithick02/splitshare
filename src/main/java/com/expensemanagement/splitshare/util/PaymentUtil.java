package com.expensemanagement.splitshare.util;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.apache.commons.lang3.tuple.MutablePair;
import org.apache.commons.lang3.tuple.Pair;
import org.springframework.stereotype.Component;

@Component
public class PaymentUtil {

    public void addUserPaymentsToMap(Map<Long, List<Pair<Long, Double>>> userPaymentsTrack, Long fromUserId, Long toUserId, Double amountOwed) {
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

    public Map<Long, List<Pair<Long, Double>>> reverseUserSplitDebtorGraph(Map<Long, List<Pair<Long, Double>>> userSplitDebtorGraph) {
        Map<Long, List<Pair<Long, Double>>> reverseGraph = new HashMap<>();
        for (Map.Entry<Long, List<Pair<Long, Double>>> debtorPaymentTrack : userSplitDebtorGraph.entrySet()) {
            for (Pair<Long, Double> debtorPayment : debtorPaymentTrack.getValue()) {
                addUserPaymentsToMap(reverseGraph, debtorPayment.getLeft(), debtorPaymentTrack.getKey(), debtorPayment.getRight());
            }
        }
        return reverseGraph;
    }
}
