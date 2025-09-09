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
        
        return enchantedBookSplitting || toolEnchantmentSplitting;
    }
    
    private void processSplitting(PlayerEntity player, AnvilScreenHandler handler, ItemStack leftStack, ItemStack rightStack, ItemStack outputStack) {
        ItemEnchantmentsComponent originalEnchantments = 
            EnchantmentHelper.getEnchantments(leftStack);
        Set<RegistryEntry<Enchantment>> enchantmentSet = originalEnchantments.getEnchantments();
        
        boolean isToolSplitting = !leftStack.isOf(Items.ENCHANTED_BOOK);
        String itemType = isToolSplitting ? "工具" : "附魔书";
        
        // 附魔书和工具的拆分规则不同
        if (isToolSplitting) {
            // 工具拆附魔：任何附魔数量都可以拆分（包括单个附魔）
            if (enchantmentSet.size() <= 1) {
                // 创建无附魔的工具
                ItemStack cleanTool = leftStack.copy();
                EnchantmentHelper.set(cleanTool, ItemEnchantmentsComponent.DEFAULT);
                handler.getSlot(0).setStack(cleanTool);
            }
        } else {
            // 附魔书拆分：只有多个附魔才能拆分
            if (enchantmentSet.size() <= 1) {
                handler.getSlot(0).setStack(ItemStack.EMPTY);
            }
        }
        
        // 对于多附魔的情况（附魔书或工具），统一处理
        if (enchantmentSet.size() > 1) {
            // 创建剩余的附魔（去除第一个附魔）
            ItemEnchantmentsComponent.Builder builder = new ItemEnchantmentsComponent.Builder(ItemEnchantmentsComponent.DEFAULT);
            boolean first = true;
            
            for (RegistryEntry<Enchantment> enchantment : enchantmentSet) {
                int level = originalEnchantments.getLevel(enchantment);
                if (first) {
                    first = false;
                    continue; // 跳过第一个附魔（已被拆出）
                }
                builder.add(enchantment, level);
            }
            
            ItemEnchantmentsComponent remainingEnchantments = builder.build();
            
            // 更新左槽
            if (!remainingEnchantments.isEmpty()) {
                if (isToolSplitting) {
                    // 工具拆附魔：保持工具类型，但更新附魔
                    ItemStack newLeftStack = leftStack.copy();
                    EnchantmentHelper.set(newLeftStack, remainingEnchantments);
                    handler.getSlot(0).setStack(newLeftStack);
                } else {
                    // 附魔书拆分：创建附魔书
                    ItemStack newLeftStack = new ItemStack(Items.ENCHANTED_BOOK);
                    EnchantmentHelper.set(newLeftStack, remainingEnchantments);
                    handler.getSlot(0).setStack(newLeftStack);
                }
            } else {
                if (isToolSplitting) {
                    // 工具没有剩余附魔：创建无附魔版本
                    ItemStack cleanTool = leftStack.copy();
                    EnchantmentHelper.set(cleanTool, ItemEnchantmentsComponent.DEFAULT);
                    handler.getSlot(0).setStack(cleanTool);
                } else {
                    // 附魔书没有剩余附魔：清空左槽
                    handler.getSlot(0).setStack(ItemStack.EMPTY);
                }
            }
        }
        
        // 清空右槽的普通书（模拟消耗）
        handler.getSlot(1).setStack(ItemStack.EMPTY);
        
        // 给予玩家净消耗1级经验（约10经验点）
        player.addExperience(-10); // 扣除10经验点，相当于1级
        
        // 用户可见反馈
        String message = isToolSplitting ? 
            "§a[AnvilMagic] 工具拆附魔完成，净消耗 1 级" : 
            "§a[AnvilMagic] 附魔书拆分完成，净消耗 1 级";
        player.sendMessage(Text.literal(message), false);

        AnvilMagicClient.LOGGER.info("玩家 {} 成功拆分{}附魔，净消耗1级经验。原附魔: {} 剩余附魔: {}", 
            player.getName().getString(), itemType, enchantmentSet.size(), enchantmentSet.size() - 1);
    }
}