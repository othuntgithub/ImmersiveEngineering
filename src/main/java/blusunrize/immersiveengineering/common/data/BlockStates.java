/*
 * BluSunrize
 * Copyright (c) 2017
 *
 * This code is licensed under "Blu's License of Common Sense"
 * Details can be found in the license file in the root folder of this project
 */

package blusunrize.immersiveengineering.common.data;

import blusunrize.immersiveengineering.api.IEProperties;
import blusunrize.immersiveengineering.api.energy.wires.WireType;
import blusunrize.immersiveengineering.common.blocks.EnumMetals;
import blusunrize.immersiveengineering.common.blocks.IEBlocks.*;
import blusunrize.immersiveengineering.common.blocks.generic.IEFenceBlock;
import blusunrize.immersiveengineering.common.blocks.generic.PostBlock;
import blusunrize.immersiveengineering.common.blocks.generic.WallmountBlock;
import blusunrize.immersiveengineering.common.blocks.generic.WallmountBlock.Orientation;
import blusunrize.immersiveengineering.common.blocks.metal.MetalLadderBlock.CoverType;
import blusunrize.immersiveengineering.common.data.Models.MetalModels;
import blusunrize.immersiveengineering.common.data.blockstate.BlockstateGenerator;
import blusunrize.immersiveengineering.common.data.blockstate.VariantBlockstate.Builder;
import blusunrize.immersiveengineering.common.data.model.ModelFile;
import blusunrize.immersiveengineering.common.data.model.ModelFile.ExistingModelFile;
import blusunrize.immersiveengineering.common.data.model.ModelHelper.BasicStairsShape;
import com.google.common.base.Preconditions;
import com.google.common.collect.ImmutableMap;
import net.minecraft.block.Block;
import net.minecraft.block.SlabBlock;
import net.minecraft.block.StairsBlock;
import net.minecraft.data.DataGenerator;
import net.minecraft.state.BooleanProperty;
import net.minecraft.state.EnumProperty;
import net.minecraft.state.IProperty;
import net.minecraft.state.properties.Half;
import net.minecraft.state.properties.SlabType;
import net.minecraft.state.properties.StairsShape;
import net.minecraft.util.Direction;
import net.minecraft.util.ResourceLocation;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.function.BiConsumer;

import static blusunrize.immersiveengineering.common.data.IEDataGenerator.rl;

public class BlockStates extends BlockstateGenerator
{
	private static final ConfiguredModel EMPTY_MODEL = new ConfiguredModel(new ExistingModelFile(rl("block/ie_empty")));
	private final Models models;

	public BlockStates(DataGenerator gen, Models models)
	{
		super(gen);
		this.models = models;
	}

	@Override
	protected void registerStates(BiConsumer<Block, IVariantModelGenerator> variantBased, BiConsumer<Block, List<MultiPart>> multipartBased)
	{
		for(EnumMetals m : EnumMetals.values())
		{
			MetalModels metalModels = models.metalModels.get(m);
			if(!m.isVanillaMetal())
			{
				if(m.shouldAddOre())
					createBasicBlock(Metals.ores.get(m), metalModels.ore, variantBased);
				createBasicBlock(Metals.storage.get(m), metalModels.storage, variantBased);
			}
			createBasicBlock(Metals.sheetmetal.get(m), metalModels.sheetmetal, variantBased);
		}
		createFenceBlock(WoodenDecoration.treatedFence, models.treatedFencePost, models.treatedFenceSide, multipartBased);
		createFenceBlock(MetalDecoration.steelFence, models.steelFencePost, models.steelFenceSide, multipartBased);
		createFenceBlock(MetalDecoration.aluFence, models.aluFencePost, models.aluFenceSide, multipartBased);
		for(Entry<Block, ModelFile> entry : models.simpleBlocks.entrySet())
			createBasicBlock(entry.getKey(), entry.getValue(), variantBased);
		for(Entry<Block, Map<SlabType, ModelFile>> entry : models.slabs.entrySet())
			createSlabBlock(entry.getKey(), entry.getValue(), SlabBlock.TYPE, variantBased);
		for(Entry<Block, Map<BasicStairsShape, ModelFile>> entry : models.stairs.entrySet())
			createStairsBlock(entry.getKey(), entry.getValue(), StairsBlock.FACING, StairsBlock.HALF, StairsBlock.SHAPE, variantBased);

		createMultiblock(Multiblocks.excavator, new ExistingModelFile(rl("block/metal_multiblock/excavator.obj")),
				new ExistingModelFile(rl("block/metal_multiblock/excavator_mirrored.obj")),
				variantBased);
		createMultiblock(Multiblocks.crusher, new ExistingModelFile(rl("block/metal_multiblock/crusher_mirrored.obj")),
				new ExistingModelFile(rl("block/metal_multiblock/crusher.obj")),
				variantBased);
		createMultiblock(Multiblocks.metalPress, new ExistingModelFile(rl("block/metal_multiblock/metal_press.obj")), variantBased);
		createMultiblock(Multiblocks.assembler, new ExistingModelFile(rl("block/metal_multiblock/assembler.obj")), variantBased);
		{
			IVariantModelGenerator gen = new Builder(Multiblocks.bucketWheel)
					.setForAllWithState(ImmutableMap.of(), EMPTY_MODEL)
					.build();
			variantBased.accept(Multiblocks.bucketWheel, gen);
		}
		createMultiblock(Multiblocks.arcFurnace, new ExistingModelFile(rl("block/metal_multiblock/arc_furnace.obj")),
				new ExistingModelFile(rl("block/metal_multiblock/arc_furnace_mirrored.obj")), variantBased);

		createMultiblock(Multiblocks.blastFurnaceAdv, new ExistingModelFile(rl("block/blastfurnace_advanced.obj")), variantBased);
		createMultiblock(Multiblocks.cokeOven, models.cokeOvenOff, models.cokeOvenOn, IEProperties.MULTIBLOCKSLAVE,
				IEProperties.FACING_HORIZONTAL, IEProperties.ACTIVE, 180, variantBased);
		createMultiblock(Multiblocks.alloySmelter, models.alloySmelterOff, models.alloySmelterOn, IEProperties.MULTIBLOCKSLAVE,
				IEProperties.FACING_HORIZONTAL, IEProperties.ACTIVE, 180, variantBased);
		createMultiblock(Multiblocks.blastFurnace, models.blastFurnaceOff, models.blastFurnaceOn, IEProperties.MULTIBLOCKSLAVE,
				IEProperties.FACING_HORIZONTAL, IEProperties.ACTIVE, 180, variantBased);
		createMultiblock(MetalDevices.sampleDrill, new ExistingModelFile(rl("block/metal_device/core_drill.obj")),
				null, IEProperties.MULTIBLOCKSLAVE, IEProperties.FACING_HORIZONTAL, null, 180,
				variantBased);

		createPostBlock(MetalDecoration.aluPost, new ExistingModelFile(rl("block/wooden_device/wooden_post.obj.ie")),
				rl("block/metal_decoration/aluminum_post"),
				variantBased);
		createPostBlock(MetalDecoration.steelPost, new ExistingModelFile(rl("block/wooden_device/wooden_post.obj.ie")),
				rl("block/metal_decoration/steel_post"),
				variantBased);
		createPostBlock(WoodenDecoration.treatedPost, new ExistingModelFile(rl("block/wooden_device/wooden_post.obj.ie")),
				rl("block/wooden_decoration/post"),
				variantBased);

		createDirectionalBlock(MetalDecoration.metalLadder.get(CoverType.NONE), IEProperties.FACING_HORIZONTAL,
				models.metalLadderNone, variantBased);
		createDirectionalBlock(MetalDecoration.metalLadder.get(CoverType.ALU), IEProperties.FACING_HORIZONTAL,
				models.metalLadderAlu, variantBased);
		createDirectionalBlock(MetalDecoration.metalLadder.get(CoverType.STEEL), IEProperties.FACING_HORIZONTAL,
				models.metalLadderSteel, variantBased);

		createWallmount(WoodenDevices.treatedWallmount, rl("block/wooden_device/wallmount"), variantBased);
		createWallmount(MetalDecoration.aluWallmount, rl("block/metal_decoration/aluminum_wallmount"), variantBased);
		createWallmount(MetalDecoration.steelWallmount, rl("block/metal_decoration/steel_wallmount"), variantBased);

		//TODO proper models
		createConnector(Connectors.getEnergyConnector(WireType.LV_CATEGORY, false), new ResourceLocation("block/lever"),
				ImmutableMap.of(), variantBased);
		createConnector(Connectors.getEnergyConnector(WireType.LV_CATEGORY, true), new ResourceLocation("block/lever"),
				//ImmutableMap.of("#immersiveengineering:block/connector/connector_lv", "immersiveengineering:block/connector/relay_lv"),
				ImmutableMap.of(), variantBased);
	}

	private void createBasicBlock(Block block, ModelFile model, BiConsumer<Block, IVariantModelGenerator> out)
	{
		ConfiguredModel configuredModel = new ConfiguredModel(model);
		createBasicBlock(block, configuredModel, out);
	}

	private void createBasicBlock(Block block, ConfiguredModel model, BiConsumer<Block, IVariantModelGenerator> out)
	{
		IVariantModelGenerator gen = new Builder(block)
				.setModel(block.getDefaultState(), model)
				.build();
		out.accept(block, gen);
	}

	private void createSlabBlock(Block block, Map<SlabType, ModelFile> baseModels, EnumProperty<SlabType> typeProp, BiConsumer<Block, IVariantModelGenerator> out)
	{
		Builder b = new Builder(block);
		for(SlabType type : SlabType.values())
		{
			Map<IProperty<?>, ?> partialState = ImmutableMap.<IProperty<?>, Object>builder()
					.put(typeProp, type)
					.build();
			b.setForAllWithState(partialState, new ConfiguredModel(baseModels.get(type)));
		}
		out.accept(block, b.build());
	}

	private void createStairsBlock(Block block, Map<BasicStairsShape, ModelFile> baseModels, EnumProperty<Direction> facingProp,
								   EnumProperty<Half> halfProp, EnumProperty<StairsShape> shapeProp, BiConsumer<Block, IVariantModelGenerator> out)
	{
		Builder b = new Builder(block);
		for(Direction dir : Direction.BY_HORIZONTAL_INDEX)
		{
			for(Half half : Half.values())
			{
				for(StairsShape shape : StairsShape.values())
				{
					Map<IProperty<?>, ?> partialState = ImmutableMap.<IProperty<?>, Object>builder()
							.put(facingProp, dir)
							.put(halfProp, half)
							.put(shapeProp, shape)
							.build();
					ModelFile base = baseModels.get(BasicStairsShape.toBasicShape(shape));
					int xRot = 0;
					if(half==Half.TOP)
						xRot = 180;
					int yRot = getAngle(dir, 90);
					if(shape==StairsShape.INNER_LEFT||shape==StairsShape.OUTER_LEFT)
						yRot = (yRot+270)%360;
					b.setForAllWithState(partialState, new ConfiguredModel(base, xRot, yRot, true, ImmutableMap.of()));
				}
			}
		}
		out.accept(block, b.build());
	}

	private void createFenceBlock(IEFenceBlock block, ModelFile post, ModelFile side, BiConsumer<Block, List<MultiPart>> out)
	{
		List<MultiPart> parts = new ArrayList<>();
		ConfiguredModel postModel = new ConfiguredModel(post, 0, 0, false, ImmutableMap.of());
		parts.add(new MultiPart(postModel, false));
		for(Direction dir : Direction.BY_HORIZONTAL_INDEX)
		{
			int angle = getAngle(dir, 180);
			ConfiguredModel sideModel = new ConfiguredModel(side, 0, angle, true, ImmutableMap.of());
			BooleanProperty sideActive = block.getFacingStateMap().get(dir);
			parts.add(new MultiPart(sideModel, false, new PropertyWithValues<>(sideActive, true)));
		}
		out.accept(block, parts);
	}

	private void createMultiblock(Block b, ModelFile masterModel, ModelFile mirroredModel, int rotationOffset,
								  BiConsumer<Block, IVariantModelGenerator> out)
	{
		createMultiblock(b, masterModel, mirroredModel, IEProperties.MULTIBLOCKSLAVE, IEProperties.FACING_HORIZONTAL, IEProperties.MIRRORED, rotationOffset, out);
	}

	private void createMultiblock(Block b, ModelFile masterModel, ModelFile mirroredModel,
								  BiConsumer<Block, IVariantModelGenerator> out)
	{
		createMultiblock(b, masterModel, mirroredModel, 180, out);
	}

	private void createMultiblock(Block b, ModelFile masterModel, BiConsumer<Block, IVariantModelGenerator> out)
	{
		createMultiblock(b, masterModel, null, IEProperties.MULTIBLOCKSLAVE, IEProperties.FACING_HORIZONTAL, null, 180, out);
	}

	private void createMultiblock(Block b, ModelFile masterModel, @Nullable ModelFile mirroredModel, IProperty<Boolean> isSlave,
								  EnumProperty<Direction> facing, @Nullable IProperty<Boolean> mirroredState, int rotationOffset,
								  BiConsumer<Block, IVariantModelGenerator> out)
	{
		Preconditions.checkArgument((mirroredModel==null)==(mirroredState==null));
		Builder builder = new Builder(b);
		builder.setForAllWithState(ImmutableMap.of(isSlave, true), EMPTY_MODEL);
		boolean[] possibleMirrorStates;
		if(mirroredState!=null)
			possibleMirrorStates = new boolean[]{false, true};
		else
			possibleMirrorStates = new boolean[1];
		for(boolean mirrored : possibleMirrorStates)
			for(Direction dir : Direction.BY_HORIZONTAL_INDEX)
			{
				int angle = getAngle(dir, rotationOffset);
				ModelFile model = mirrored?mirroredModel: masterModel;
				ImmutableMap.Builder<IProperty<?>, Comparable<?>> partialState = ImmutableMap.builder();
				partialState.put(isSlave, Boolean.FALSE)
						.put(facing, dir);
				if(mirroredState!=null)
					partialState.put(mirroredState, mirrored);
				builder.setForAllWithState(partialState.build(),
						new ConfiguredModel(model, 0, angle, true, ImmutableMap.of("flip-v", true)));
			}
		out.accept(b, builder.build());
	}

	private void createPostBlock(Block b, ModelFile masterModel, ResourceLocation texture, BiConsumer<Block, IVariantModelGenerator> out)
	{
		Builder builder = new Builder(b);
		for(int i : PostBlock.POST_SLAVE.getAllowedValues())
			if(i!=0)
				builder.setForAllWithState(ImmutableMap.of(PostBlock.POST_SLAVE, i), EMPTY_MODEL);
		builder.setForAllWithState(ImmutableMap.of(PostBlock.POST_SLAVE, 0),
				new ConfiguredModel(masterModel, 0, 0, true, ImmutableMap.of("flip-v", true),
						ImmutableMap.of("#immersiveengineering:block/wooden_decoration/post", texture.toString())));
		out.accept(b, builder.build());
	}

	private int getAngle(Direction dir, int offset)
	{
		return (int)((dir.getHorizontalAngle()+offset)%360);
	}

	private void createDirectionalBlock(Block b, IProperty<Direction> prop, ModelFile model, BiConsumer<Block, IVariantModelGenerator> out)
	{
		Builder builder = new Builder(b);
		for(Direction d : Direction.BY_HORIZONTAL_INDEX)
			builder.setForAllWithState(ImmutableMap.of(prop, d), new ConfiguredModel(model, 0, getAngle(d, 180),
					true, ImmutableMap.of()));
		out.accept(b, builder.build());
	}

	private void createWallmount(Block b, ResourceLocation texture, BiConsumer<Block, IVariantModelGenerator> out)
	{
		Builder builder = new Builder(b);
		for(Direction d : Direction.BY_HORIZONTAL_INDEX)
		{
			int rotation = getAngle(d, 0);
			for(WallmountBlock.Orientation or : Orientation.values())
			{
				ResourceLocation model = rl("block/wooden_device/wallmount"+or.modelSuffix()+".obj");
				builder.setForAllWithState(
						ImmutableMap.of(IEProperties.FACING_HORIZONTAL, d, WallmountBlock.ORIENTATION, or),
						new ConfiguredModel(new ExistingModelFile(model), 0, rotation, true,
								ImmutableMap.of("flip-v", true),
								ImmutableMap.of("#immersiveengineering:block/wooden_device/wallmount", texture.toString())));
			}
		}
		out.accept(b, builder.build());
	}

	private void createConnector(Block b, ResourceLocation model, ImmutableMap<String, String> textures, BiConsumer<Block, IVariantModelGenerator> out)
	{
		final ExistingModelFile connFile = new ExistingModelFile(rl("connector"));
		//TODO layers
		final ImmutableMap<String, Object> customData = ImmutableMap.of("flip-v", true, "base", model.toString(), "layers", "SOLID");
		Builder builder = new Builder(b);
		builder.setForAllWithState(ImmutableMap.of(IEProperties.FACING_ALL, Direction.DOWN),
				new ConfiguredModel(connFile, 0, 0, true, customData, textures));
		builder.setForAllWithState(ImmutableMap.of(IEProperties.FACING_ALL, Direction.UP),
				new ConfiguredModel(connFile, 0, 180, true, customData, textures));
		for(Direction d : Direction.BY_HORIZONTAL_INDEX)
		{
			int rotation = getAngle(d, 0);
			builder.setForAllWithState(
					ImmutableMap.of(IEProperties.FACING_ALL, d),
					new ConfiguredModel(connFile, 90, rotation, true, customData, textures));
		}
		out.accept(b, builder.build());
	}
}