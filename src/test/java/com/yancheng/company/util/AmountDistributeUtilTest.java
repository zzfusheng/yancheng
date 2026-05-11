package com.yancheng.company.util;

import org.junit.Test;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.junit.Assert.assertEquals;

public class AmountDistributeUtilTest {

    /** 简单 DTO，持有一个金额字段 */
    static class Item {
        BigDecimal amount;

        Item(String amount) {
            this.amount = new BigDecimal(amount);
        }

        BigDecimal getAmount() {
            return amount;
        }

        void setAmount(BigDecimal amount) {
            this.amount = amount;
        }
    }

    private List<Item> items(String... amounts) {
        Item[] arr = new Item[amounts.length];
        for (int i = 0; i < amounts.length; i++) {
            arr[i] = new Item(amounts[i]);
        }
        return Arrays.asList(arr);
    }

    // ------------------------------------------------------------------ //
    // 场景 1：某对象金额 > 剩余金额 → 截断为剩余金额并终止                   //
    // items=[50, 80, 30], fixedAmount=100                                  //
    // 期望：[50, 50, 30不变]，第二个被截断后停止                              //
    // ------------------------------------------------------------------ //
    @Test
    public void testItemExceedsRemaining() {
        List<Item> list = items("50", "80", "30");
        AmountDistributeUtil.distribute(
                list,
                new BigDecimal("100"),
                Item::getAmount,
                (item, remaining) -> item.setAmount(remaining) // remaining = 100-50 = 50
        );
        assertEquals(new BigDecimal("50"), list.get(0).getAmount());
        assertEquals(new BigDecimal("50"), list.get(1).getAmount()); // 截断为剩余 50
        assertEquals(new BigDecimal("30"), list.get(2).getAmount()); // 未被修改
    }

    // ------------------------------------------------------------------ //
    // 场景 2：固定金额恰好全部分配完                                          //
    // items=[30, 20, 50], fixedAmount=100                                  //
    // 期望：三个金额都不变，分配完毕后停止                                      //
    // ------------------------------------------------------------------ //
    @Test
    public void testExactlyExhausted() {
        List<Item> list = items("30", "20", "50");
        AmountDistributeUtil.distribute(
                list,
                new BigDecimal("100"),
                Item::getAmount,
                (item, remaining) -> { /* 不应触发 */ throw new AssertionError("should not be called"); }
        );
        assertEquals(new BigDecimal("30"), list.get(0).getAmount());
        assertEquals(new BigDecimal("20"), list.get(1).getAmount());
        assertEquals(new BigDecimal("50"), list.get(2).getAmount());
    }

    // ------------------------------------------------------------------ //
    // 场景 3：固定金额大于所有对象金额之和 → 所有对象金额不变                   //
    // items=[10, 20], fixedAmount=100                                       //
    // ------------------------------------------------------------------ //
    @Test
    public void testFixedAmountMoreThanTotal() {
        List<Item> list = items("10", "20");
        AmountDistributeUtil.distribute(
                list,
                new BigDecimal("100"),
                Item::getAmount,
                (item, remaining) -> { throw new AssertionError("should not be called"); }
        );
        assertEquals(new BigDecimal("10"), list.get(0).getAmount());
        assertEquals(new BigDecimal("20"), list.get(1).getAmount());
    }

    // ------------------------------------------------------------------ //
    // 场景 4：第一个对象金额即 > 固定金额 → 直接截断并停止                     //
    // items=[200, 50], fixedAmount=100                                      //
    // ------------------------------------------------------------------ //
    @Test
    public void testFirstItemExceedsFixed() {
        List<Item> list = items("200", "50");
        AmountDistributeUtil.distribute(
                list,
                new BigDecimal("100"),
                Item::getAmount,
                (item, remaining) -> item.setAmount(remaining)
        );
        assertEquals(new BigDecimal("100"), list.get(0).getAmount());
        assertEquals(new BigDecimal("50"), list.get(1).getAmount()); // 未被修改
    }

    // ------------------------------------------------------------------ //
    // 场景 5：空列表 / null → 不抛异常                                       //
    // ------------------------------------------------------------------ //
    @Test
    public void testEmptyList() {
        AmountDistributeUtil.distribute(
                Collections.emptyList(),
                new BigDecimal("100"),
                item -> null,
                (item, remaining) -> { }
        );
        // no exception expected
    }

    @Test
    public void testNullList() {
        AmountDistributeUtil.distribute(
                null,
                new BigDecimal("100"),
                item -> null,
                (item, remaining) -> { }
        );
        // no exception expected
    }
}
