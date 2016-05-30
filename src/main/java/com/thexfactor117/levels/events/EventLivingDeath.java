package com.thexfactor117.levels.events;

import java.util.Random;

import com.thexfactor117.levels.handlers.ConfigHandler;
import com.thexfactor117.levels.helpers.AbilityHelper;
import com.thexfactor117.levels.helpers.Experience;
import com.thexfactor117.levels.helpers.NBTHelper;
import com.thexfactor117.levels.helpers.Rarity;

import net.minecraft.entity.monster.EntityMob;
import net.minecraft.entity.passive.EntityAnimal;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.projectile.EntityArrow;
import net.minecraft.init.SoundEvents;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemSword;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.event.entity.living.LivingDeathEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

/**
 * 
 * @author TheXFactor117
 *
 */
public class EventLivingDeath 
{
	/**
	 * Called when the specifed entity dies by another specific source. In this case, the source
	 * is the player.
	 * @param event
	 */
	@SubscribeEvent
	public void onEntityDeath(LivingDeathEvent event)
	{	
		/*****************
		 * MELEE WEAPONS *
		 *****************/
		if (event.getSource().getSourceOfDamage() instanceof EntityPlayer)
		{
			EntityPlayer player = (EntityPlayer) event.getSource().getSourceOfDamage();
			Random rand = player.worldObj.rand;
			ItemStack stack = player.inventory.getCurrentItem();
			
			if (stack != null)
			{
				if (stack.getItem() instanceof ItemSword)
				{
					NBTTagCompound nbt = NBTHelper.loadStackNBT(stack);
					
					if (nbt != null)
					{
						Rarity rarity = Rarity.getRarity(nbt);
						int level = Experience.getLevel(nbt);
						int experience = Experience.getExperience(nbt);

						/*
						 * Rarities
						 */
						if (rarity == Rarity.UNKNOWN)
						{
							rarity = Rarity.getRandomRarity(rand);
							rarity.setRarity(nbt);
							if (rarity == Rarity.ANCIENT) player.worldObj.playSound(player, player.getPosition(), SoundEvents.ENTITY_ENDERDRAGON_DEATH, player.getSoundCategory(), 0.8F, 1.0F);
						}

						/*
						 * Weapon Bonus Experience
						 */
						if (level < ConfigHandler.maxLevelCap)
						{
							if (event.getEntityLiving() instanceof EntityMob)
							{
								Experience.setExperience(nbt, Experience.getExperience(nbt) + ConfigHandler.monsterBonusExp);
							}

							if (event.getEntityLiving() instanceof EntityAnimal)
							{
								Experience.setExperience(nbt, Experience.getExperience(nbt) + ConfigHandler.animalBonusExp);
							}
						}

						/*
						 * Leveling system
						 */
						level = Experience.getNextLevel(player, nbt, AbilityHelper.ABILITIES, level, experience, rand);
						Experience.setLevel(nbt, level);
						
						NBTHelper.saveStackNBT(stack, nbt);
					}
				}
			}
		}
		
		/********
		 * BOWS *
		 *******/
		if (event.getSource().getSourceOfDamage() instanceof EntityArrow)
		{
			EntityArrow arrow = (EntityArrow) event.getSource().getSourceOfDamage();
			
			if (arrow.shootingEntity instanceof EntityPlayer)
			{
				EntityPlayer player = (EntityPlayer) arrow.shootingEntity;
				Random rand = player.worldObj.rand;
				ItemStack stack = player.inventory.getCurrentItem();
				
				if (stack != null)
				{
					NBTTagCompound nbt = NBTHelper.loadStackNBT(stack);
					Rarity rarity = Rarity.getRarity(nbt);
					int level = Experience.getLevel(nbt);
					int experience = Experience.getExperience(nbt);
					
					/*
					 * Rarities
					 */
					if (rarity == Rarity.UNKNOWN)
					{
						rarity = Rarity.getRandomRarity(rand);
						rarity.setRarity(nbt);
						if (rarity == Rarity.ANCIENT) player.worldObj.playSound(player, player.getPosition(), SoundEvents.ENTITY_ENDERDRAGON_DEATH, player.getSoundCategory(), 0.8F, 1.0F);
					}
					
					/*
					 * Bow bonus experience
					 */
					if (level < ConfigHandler.maxLevelCap)
					{
						if (event.getEntityLiving() instanceof EntityMob)
						{
							Experience.setExperience(nbt, Experience.getExperience(nbt) + ConfigHandler.monsterBonusExp);
						}

						if (event.getEntityLiving() instanceof EntityAnimal)
						{
							Experience.setExperience(nbt, Experience.getExperience(nbt) + ConfigHandler.animalBonusExp);
						}
					}
					
					/*
					 * Leveling experience
					 */
					level = Experience.getNextLevel(player, nbt, AbilityHelper.ABILITIES, level, experience, rand);
					Experience.setLevel(nbt, level);
					
					NBTHelper.saveStackNBT(stack, nbt);
				}
			}
		}
	}
}
