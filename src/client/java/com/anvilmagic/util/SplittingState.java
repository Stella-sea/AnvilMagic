package com.anvilmagic.util;

/**
 * 用于管理拆分状态的工具类
 * 解决客户端和服务端线程间通信问题
 */
public class SplittingState {
    
    // 使用volatile确保跨线程可见性
    private static volatile boolean justCompletedSplitting = false;
    
    /**
     * 设置刚完成拆分标记
     */
    public static void setJustCompletedSplitting(boolean value) {
        justCompletedSplitting = value;
        System.out.println("[AnvilMagic] 设置静态标记 justCompletedSplitting = " + value);
    }
    
    /**
     * 检查并重置刚完成拆分标记
     * @return 如果刚完成拆分返回true并重置标记，否则返回false
     */
    public static boolean checkAndResetSplittingFlag() {
        if (justCompletedSplitting) {
            justCompletedSplitting = false;
            return true;
        }
        return false;
    }
}