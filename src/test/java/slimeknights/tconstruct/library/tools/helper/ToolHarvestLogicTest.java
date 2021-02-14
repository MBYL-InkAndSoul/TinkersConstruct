package slimeknights.tconstruct.library.tools.helper;

import com.google.common.collect.ImmutableList;
import net.minecraft.block.Blocks;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraftforge.common.ToolType;
import org.junit.jupiter.api.Test;
import slimeknights.tconstruct.fixture.MaterialItemFixture;
import slimeknights.tconstruct.fixture.MaterialStatsFixture;
import slimeknights.tconstruct.fixture.ToolDefinitionFixture;
import slimeknights.tconstruct.library.tools.ToolBaseStatDefinition;
import slimeknights.tconstruct.library.tools.ToolCoreTest;
import slimeknights.tconstruct.library.tools.ToolDefinition;
import slimeknights.tconstruct.library.tools.item.ToolCore;
import slimeknights.tconstruct.tools.harvest.PickaxeTool;

import static org.assertj.core.api.Assertions.assertThat;

class ToolHarvestLogicTest extends ToolCoreTest {

  private final PickaxeTool pickaxeTool = new PickaxeTool(
    new Item.Properties().addToolType(ToolType.PICKAXE, 1),
    ToolDefinitionFixture.getStandardToolDefinition());
  private final ToolHarvestLogic toolHarvestLogic = new ToolHarvestLogic();

  @Test
  void calcSpeed_dirt_notEffective() {
    ItemStack tool = buildTestTool(pickaxeTool);

    float speed = toolHarvestLogic.calcDigSpeed(tool, Blocks.DIRT.getDefaultState());

    assertThat(speed).isEqualTo(1f);
  }

  @Test
  void calcSpeed_cobble_effective() {
    ItemStack tool = buildTestTool(pickaxeTool);

    float speed = toolHarvestLogic.calcDigSpeed(tool, Blocks.COBBLESTONE.getDefaultState());

    assertThat(speed).isEqualTo(MaterialStatsFixture.MATERIAL_STATS_HEAD.getMiningSpeed());
  }

  @Test
  void calcSpeed_obsidian_notEnoughHarvestLevel() {
    ItemStack tool = buildTestTool(pickaxeTool);

    float speed = toolHarvestLogic.calcDigSpeed(tool, Blocks.OBSIDIAN.getDefaultState());

    assertThat(speed).isEqualTo(1f);
  }

  @Test
  void calcSpeed_broken_slowButNotZero() {
    ItemStack tool = buildTestTool(pickaxeTool);
    breakTool(tool);

    float speed = toolHarvestLogic.calcDigSpeed(tool, Blocks.DIRT.getDefaultState());

    assertThat(speed).isLessThan(1f);
    assertThat(speed).isGreaterThan(0f);
  }

  @Test
  void calcSpeed_effective_withMiningModifier() {
    float modifier = 2f;
    ToolCore toolWithMiningModifier = new PickaxeTool(
      new Item.Properties().addToolType(ToolType.PICKAXE, 1),
      new ToolDefinition(
        new ToolBaseStatDefinition.Builder().setDamageModifier(1f).setMiningSpeedModifier(modifier).build(),
        () -> ImmutableList.of(MaterialItemFixture.MATERIAL_ITEM_HEAD, MaterialItemFixture.MATERIAL_ITEM_HANDLE, MaterialItemFixture.MATERIAL_ITEM_EXTRA)
      ));
    ItemStack tool = buildTestTool(toolWithMiningModifier);

    float speed = toolHarvestLogic.calcDigSpeed(tool, Blocks.COBBLESTONE.getDefaultState());

    assertThat(speed).isEqualTo(MaterialStatsFixture.MATERIAL_STATS_HEAD.getMiningSpeed() * modifier);
  }
}