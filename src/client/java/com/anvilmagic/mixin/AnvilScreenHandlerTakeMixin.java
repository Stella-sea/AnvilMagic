package com.anvilmagic.mixin;

import com.anvilmagic.AnvilMagicClient;
import com.anvilmagic.util.SplittingState;
import net.minecraft.component.type.ItemEnchantmentsComponent;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.screen.AnvilScreenHandler;
import net.minecraft.text.Text;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.Set;

@Mixin(AnvilScreenHandler.class)
public class AnvilScreenHandlerTakeMixin {
    
    @Inject(method = "onTakeOutput", at = @At("HEAD"), cancellable = true)
    private void onTakeOutput(PlayerEntity player, ItemStack stack, CallbackInfo ci) {
        AnvilScreenHandler handler = (AnvilScreenHandler) (Object) this;
        ItemStack leftStack = handler.getSlot(0).getStack();
        ItemStack rightStack = handler.getSlot(1).getStack();
        
        // 调试日志：确认注入点被调用
        System.out.println("[AnvilMagic] onTakeOutput called, left=" + leftStack.getItem() + " right=" + rightStack.getItem() + " output=" + stack.getItem());
        
        // 检查是否是拆分附魔书的操作
        if (shouldProcessSplitting(leftStack, rightStack, stack)) {
            // 取消原生的onTakeOutput逻辑，由我们完全控制
            ci.cancel();
            
            // 执行我们的拆分逻辑
            processSplitting(player, handler, leftStack, rightStack, stack);
            
            // 设置静态标记，告知AnvilScreenHandlerMixin我们刚完成了拆分
            SplittingState.setJustCompletedSplitting(true);
        }
    }
    
    private boolean shouldProcessSplitting(ItemStack leftStack, ItemStack rightStack, ItemStack outputStack) {
        // 通用拆附魔检测 - 支持所有可附魔物品
        boolean leftHasEnchantments = !EnchantmentHelper.getEnchantments(leftStack).isEmpty();
        boolean rightBook = rightStack.isOf(Items.BOOK); // 右槽必须是普通书
        boolean leftIsNotEnchantedBook = !leftStack.isOf(Items.ENCHANTED_BOOK);
        
        // 检查是否为附魔书拆分（原有功能）
        boolean enchantedBookSplitting = leftStack.isOf(Items.ENCHANTED_BOOK) && leftHasEnchantments && rightBook && outputStack.isOf(Items.ENCHANTED_BOOK);
        
        // 检查是否为工具拆附魔（新功能）
        boolean toolEnchantmentSplitting = leftIsNotEnchantedBook && leftHasEnchantments && rightBook && outputStack.isOf(Items.ENCHANTED_BOOK);
        
        System.out.println("[AnvilMagic] shouldProcessSplitting: leftHasEnchantments=" + leftHasEnchantments + " (size=" + EnchantmentHelper.getEnchantments(leftStack).getEnchantments().size() + ") rightBook=" + rightBook + " enchantedBookSplitting=" + enchantedBookSplitting + " toolEnchantmentSplitting=" + toolEnchantmentSplitting);
        
        return enchantedBookSplitting || toolEnchantmentSplitting;
    }
    
    private void processSplitting(PlayerEntity player, AnvilScreenHandler handler, ItemStack leftStack, ItemStack rightStack, ItemStack outputStack) {
        ItemEnchantmentsComponent originalEnchantments = 
            EnchantmentHelper.getEnchantments(leftStack);
        Set<RegistryEntry<Enchantment>> enchantmentSet = originalEnchantments.getEnchantments();
        
        boolean isToolSplitting = !leftStack.isOf(Items.ENCHANTED_BOOK);
        String itemType = isToolSplitting ? "工具" : "附魔书";
        
        System.out.println("[AnvilMagic] =====开始处理" + itemType + "拆分后的清理工作=====");
        System.out.println("[AnvilMagic] 原" + itemType + "信息: item=" + leftStack.getItem() + " 附魔数=" + enchantmentSet.size());
        
        // 打印所有附魔信息
        int index = 0;
        for (RegistryEntry<Enchantment> enchantment : enchantmentSet) {
            int level = originalEnchantments.getLevel(enchantment);
            System.out.println("[AnvilMagic] 附魔[" + index + "]: " + enchantment.getIdAsString() + " 等级=" + level);
            index++;
        }
        
        // 附魔书和工具的拆分规则不同
        if (isToolSplitting) {
            // 工具拆附魔：任何附魔数量都可以拆分（包括单个附魔）
            if (enchantmentSet.size() <= 1) {
                System.out.println("[AnvilMagic] 单个附魔工具，创建无附魔版本");
                // 创建无附魔的工具
                ItemStack cleanTool = leftStack.copy();
                EnchantmentHelper.set(cleanTool, ItemEnchantmentsComponent.DEFAULT);
                handler.getSlot(0).setStack(cleanTool);
                System.out.println("[AnvilMagic] ✅ 创建无附魔工具: " + cleanTool.getItem());
            } else {
                System.out.println("[AnvilMagic] 多个附魔工具，开始创建剩余附魔工具");
                // 处理多附魔工具，跟下面的多附魔逻辑一样
            }
        } else {
            // 附魔书拆分：只有多个附魔才能拆分
            if (enchantmentSet.size() <= 1) {
                System.out.println("[AnvilMagic] 单个附魔书，清空左槽模拟消耗");
                handler.getSlot(0).setStack(ItemStack.EMPTY);
            } else {
                System.out.println("[AnvilMagic] 多个附魔书，开始创建剩余附魔书");
                // 处理多附魔书，跟下面的多附魔逻辑一样
            }
        }
        
        // 对于多附魔的情况（附魔书或工具），统一处理
        if (enchantmentSet.size() > 1) {
            // 创建剩余的附魔（去除第一个附魔）
            ItemEnchantmentsComponent.Builder builder = new ItemEnchantmentsComponent.Builder(ItemEnchantmentsComponent.DEFAULT);
            boolean first = true;
            int addedCount = 0;
            
            for (RegistryEntry<Enchantment> enchantment : enchantmentSet) {
                int level = originalEnchantments.getLevel(enchantment);
                if (first) {
                    System.out.println("[AnvilMagic] 跳过第一个附魔: " + enchantment.getIdAsString() + " 等级=" + level);
                    first = false;
                    continue; // 跳过第一个附魔（已被拆出）
                }
                
                System.out.println("[AnvilMagic] 添加剩余附魔: " + enchantment.getIdAsString() + " 等级=" + level);
                builder.add(enchantment, level);
                addedCount++;
            }
            
            ItemEnchantmentsComponent remainingEnchantments = builder.build();
            System.out.println("[AnvilMagic] 构建剩余附魔完成，添加了 " + addedCount + " 个附魔");
            System.out.println("[AnvilMagic] 剩余附魔是否为空: " + remainingEnchantments.isEmpty());
            System.out.println("[AnvilMagic] 剩余附魔数量: " + remainingEnchantments.getEnchantments().size());
            
            // 更新左槽
            if (!remainingEnchantments.isEmpty()) {
                if (isToolSplitting) {
                    // 工具拆附魔：保持工具类型，但更新附魔
                    ItemStack newLeftStack = leftStack.copy();
                    EnchantmentHelper.set(newLeftStack, remainingEnchantments);
                    
                    System.out.println("[AnvilMagic] 创建新左槽工具: " + newLeftStack.getItem());
                    System.out.println("[AnvilMagic] 新左槽附魔数: " + EnchantmentHelper.getEnchantments(newLeftStack).getEnchantments().size());
                    
                    handler.getSlot(0).setStack(newLeftStack);
                    System.out.println("[AnvilMagic] ✅ 成功更新左槽为剩余附魔工具");
                } else {
                    // 附魔书拆分：创建附魔书
                    ItemStack newLeftStack = new ItemStack(Items.ENCHANTED_BOOK);
                    EnchantmentHelper.set(newLeftStack, remainingEnchantments);
                    
                    System.out.println("[AnvilMagic] 创建新左槽物品: " + newLeftStack.getItem());
                    System.out.println("[AnvilMagic] 新左槽附魔数: " + EnchantmentHelper.getEnchantments(newLeftStack).getEnchantments().size());
                    
                    handler.getSlot(0).setStack(newLeftStack);
                    System.out.println("[AnvilMagic] ✅ 成功更新左槽为剩余附魔书");
                }
            } else {
                if (isToolSplitting) {
                    // 工具没有剩余附魔：创建无附魔版本
                    ItemStack cleanTool = leftStack.copy();
                    EnchantmentHelper.set(cleanTool, ItemEnchantmentsComponent.DEFAULT);
                    handler.getSlot(0).setStack(cleanTool);
                    System.out.println("[AnvilMagic] ✅ 没有剩余附魔，创建无附魔工具");
                } else {
                    // 附魔书没有剩余附魔：清空左槽
                    handler.getSlot(0).setStack(ItemStack.EMPTY);
                    System.out.println("[AnvilMagic] ❌ 没有剩余附魔，清空左槽");
                }
            }
        }
        
        // 清空右槽的普通书（模拟消耗）
        handler.getSlot(1).setStack(ItemStack.EMPTY);
        System.out.println("[AnvilMagic] 清空右槽的普通书");
        
        // 给予玩家净消耗1级经验（约10经验点）
        player.addExperience(-10); // 扣除10经验点，相当于1级
        
        // 用户可见反馈
        String message = isToolSplitting ? 
            "§a[AnvilMagic] 工具拆附魔完成，净消耗 1 级" : 
            "§a[AnvilMagic] 附魔书拆分完成，净消耗 1 级";
        player.sendMessage(Text.literal(message), false);
        
        System.out.println("[AnvilMagic] " + itemType + "拆分完成：原附魔数=" + enchantmentSet.size() + " 剩余附魔数=" + (enchantmentSet.size() - 1) + " 经验扣除=-10");

        AnvilMagicClient.LOGGER.info("玩家 {} 成功拆分{}附魔，净消耗1级经验。原附魔: {} 剩余附魔: {}", 
            player.getName().getString(), itemType, enchantmentSet.size(), enchantmentSet.size() - 1);
    }
}