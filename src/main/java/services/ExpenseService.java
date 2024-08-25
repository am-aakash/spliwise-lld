package services;

import models.Amount;
import models.BalanceMap;
import models.Expense;
import models.PaymentGraph;

import java.util.*;

import static models.Currency.*;

public class ExpenseService {
    //select * from expenses where group_id = 'groupId';
    private final Map<String, List<Expense>> groupExpenses; // groupId -> List of Expenses in group
    private final Map<String, Expense> allExpenses; // expenseId -> Expense

    public ExpenseService() {
        this.groupExpenses = new HashMap<>();
        this.allExpenses = new HashMap<>();
    }

    public void addExpense(Expense expense) {
        allExpenses.put(expense.getId(), expense);
        final var groupId = expense.getGroupId();
        if (groupId != null) {
            groupExpenses.putIfAbsent(groupId, new ArrayList<>());
            groupExpenses.get(groupId).add(expense);
        }
    }

    public void editExpense(String expenseId, Expense expense){
        
    }

    public void deleteExpense(String expenseId){

    }

    public void settleExpense(String expenseId){

    }

    public List<Expense> getGroupExpenses(String groupId) {
        return groupExpenses.get(groupId);
    }

    public PaymentGraph getPaymentGraph(BalanceMap groupBalances) {
        final Comparator<Map.Entry<String, Amount>> ascending = Comparator.comparingDouble(balance -> balance.getValue().getAmount());
        final PriorityQueue<Map.Entry<String, Amount>>
                positiveAmounts = new PriorityQueue<>(ascending.reversed()),
                negativeAmounts = new PriorityQueue<>(ascending);
        for (var balance : groupBalances.getBalances().entrySet()) {
            if (balance.getValue().getAmount() > 0) {
                positiveAmounts.add(balance);
            } else {
                negativeAmounts.add(balance);
            }
        }
        final var graph = new HashMap<String, BalanceMap>();
        while (!positiveAmounts.isEmpty()) {
            //eliminate largest from both groups
            final var largestPositive = positiveAmounts.poll();
            final var largestNegative = negativeAmounts.poll();
            final var negativeAmount = -largestNegative.getValue().getAmount();
            final var positiveAmount = largestPositive.getValue().getAmount();
            graph.putIfAbsent(largestPositive.getKey(), new BalanceMap());
            graph.get(largestPositive.getKey()).getBalances()
                    .put(largestNegative.getKey(), new Amount(USD, Math.min(positiveAmount, negativeAmount)));
            double remaining = positiveAmount - negativeAmount;
            final var remainingAmount = new Amount(USD, remaining);
            if (remaining > 0) {
                positiveAmounts.add(new AbstractMap.SimpleEntry<>(largestPositive.getKey(), remainingAmount));
            } else if (remaining < 0) {
                negativeAmounts.add(new AbstractMap.SimpleEntry<>(largestNegative.getKey(), remainingAmount));
            }
        }
        return new PaymentGraph(graph);
    }
}