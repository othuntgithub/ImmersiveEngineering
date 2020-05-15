/*
 * BluSunrize
 * Copyright (c) 2017
 *
 * This code is licensed under "Blu's License of Common Sense"
 * Details can be found in the license file in the root folder of this project
 */

package blusunrize.lib.manual.gui;

import blusunrize.immersiveengineering.dummy.GlStateManager;
import blusunrize.lib.manual.ManualUtils;
import net.minecraft.client.gui.widget.button.Button;

import static blusunrize.immersiveengineering.dummy.GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA;
import static blusunrize.immersiveengineering.dummy.GlStateManager.DestFactor.ZERO;
import static blusunrize.immersiveengineering.dummy.GlStateManager.SourceFactor.ONE;
import static blusunrize.immersiveengineering.dummy.GlStateManager.SourceFactor.SRC_ALPHA;

public class GuiButtonManual extends Button
{
	public ManualScreen gui;
	public int[] colour = {0x33000000, 0x33cb7f32};
	public int[] textColour = {0xffe0e0e0, 0xffffffa0};

	public GuiButtonManual(ManualScreen gui, int x, int y, int w, int h, String text, IPressable handler)
	{
		super(x, y, w, h, text, handler);
		this.gui = gui;
	}

	public GuiButtonManual setColour(int normal, int hovered)
	{
		colour = new int[]{normal, hovered};
		return this;
	}

	public GuiButtonManual setTextColour(int normal, int hovered)
	{
		textColour = new int[]{normal, hovered};
		return this;
	}

	@Override
	public void render(int mx, int my, float partialTicks)
	{
		if(this.visible)
		{
			ManualUtils.bindTexture(gui.texture);
			GlStateManager.color3f(1.0F, 1.0F, 1.0F);
			this.isHovered = mx >= this.x&&mx < (this.x+this.width)&&my >= this.y&&my < (this.y+this.height);
			GlStateManager.enableBlend();
			GlStateManager.blendFuncSeparate(SRC_ALPHA, ONE_MINUS_SRC_ALPHA, ONE, ZERO);

			int col = colour[isHovered?1: 0];
			fill(x, y, x+width, y+height, col);
			int txtCol = textColour[isHovered?1: 0];
			int sw = gui.manual.fontRenderer().getStringWidth(getMessage());
			gui.manual.fontRenderer().drawString(getMessage(), x+width/2-sw/2, y+height/2-gui.manual.fontRenderer().FONT_HEIGHT/2, txtCol);
			//TODO this.mouseDragged(mc, mx, my);
		}
	}
}