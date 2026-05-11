package com.yancheng.company.util;

import java.math.BigDecimal;
import java.util.List;
import java.util.function.BiConsumer;
import java.util.function.Function;

/**
 * 固定金额分配工具类
 * <p>
 * 规则：遍历对象列表，将固定金额依次分配给每个对象：
 * <ul>
 *   <li>若当前对象金额 &gt; 剩余固定金额，则将该对象金额 set 为剩余固定金额，终止循环。</li>
 *   <li>若当前对象金额 &le; 剩余固定金额，则金额不变，剩余固定金额减去本次消耗的金额，继续下一个。</li>
 *   <li>剩余固定金额耗尽时停止循环。</li>
 * </ul>
 */
public class AmountDistributeUtil {

    private AmountDistributeUtil() {
    }

    /**
     * 按固定金额依次分配给列表中的对象。
     *
     * @param items              对象列表
     * @param fixedAmount        固定总金额（必须大于 0）
     * @param getAmount          获取对象当前金额的函数
     * @param setAmountToRemaining 当对象金额超出剩余固定金额时的回调，参数为（对象, 剩余金额），
     *                             调用后终止循环
     * @param <T>                对象类型
     */
    public static <T> void distribute(List<T> items,
                                      BigDecimal fixedAmount,
                                      Function<T, BigDecimal> getAmount,
                                      BiConsumer<T, BigDecimal> setAmountToRemaining) {
        if (items == null || items.isEmpty() || fixedAmount == null
                || fixedAmount.compareTo(BigDecimal.ZERO) <= 0) {
            return;
        }

        BigDecimal remaining = fixedAmount;

        for (T item : items) {
            BigDecimal amount = getAmount.apply(item);
            if (amount == null) {
                continue;
            }

            int cmp = amount.compareTo(remaining);
            if (cmp > 0) {
                // 当前对象金额 > 剩余金额：截断为剩余金额，终止循环
                setAmountToRemaining.accept(item, remaining);
                break;
            } else {
                // 当前对象金额 <= 剩余金额：金额不变，扣减剩余金额
                remaining = remaining.subtract(amount);
                if (remaining.compareTo(BigDecimal.ZERO) == 0) {
                    // 固定金额恰好用完
                    break;
                }
            }
        }
    }
}
