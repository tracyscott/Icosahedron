package noomechanism.icosahedron.ui;

import heronarts.lx.parameter.BooleanParameter;
import heronarts.lx.studio.LXStudio;
import heronarts.p3lx.ui.UI2dContainer;
import heronarts.p3lx.ui.component.UIButton;
import heronarts.p3lx.ui.component.UICollapsibleSection;
import noomechanism.icosahedron.Icosahedron;

public class UIPreviewComponents extends UICollapsibleSection {

  public UIPreviewComponents(final LXStudio.UI ui) {
    super(ui, 0, 0, ui.leftPane.global.getContentWidth(), 50);
    setTitle("Axes");
    UI2dContainer knobsContainer = new UI2dContainer(0, 0, getContentWidth(), 20);
    knobsContainer.setLayout(UI2dContainer.Layout.HORIZONTAL);
    knobsContainer.setPadding(10, 10, 10, 10);
    knobsContainer.addToContainer(this);

    UIButton showAxesBtn = new UIButton() {
      @Override
      public void onToggle(boolean on) {
        Icosahedron.axes.showAxes = on;
      }
    }.setLabel("axes").setActive(Icosahedron.axes.showAxes);
    showAxesBtn.setWidth(35).setHeight(16);
    showAxesBtn.addToContainer(knobsContainer);
    UIButton showFloorBtn = new UIButton() {
      @Override
      public void onToggle(boolean on) {
        Icosahedron.axes.showFloor = on;
      }
    }.setLabel("floor").setActive(Icosahedron.axes.showFloor);
    showFloorBtn.setWidth(35).setHeight(16);
    showFloorBtn.addToContainer(knobsContainer);
  }
}
