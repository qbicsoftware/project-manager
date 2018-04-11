package life.qbic.module.projectOverviewModule;

import com.vaadin.data.fieldgroup.FieldGroup;
import com.vaadin.data.util.ObjectProperty;
import com.vaadin.server.Extension;
import com.vaadin.server.communication.data.RpcDataProviderExtension;
import com.vaadin.shared.ui.grid.HeightMode;
import com.vaadin.ui.Grid;
import java.util.Collection;

/**
 * Created by sven1103 on 28/11/16.
 */
public class ProjectOverviewGrid extends Grid {

  public final ObjectProperty<Boolean> isChanged = new ObjectProperty<>(true, Boolean.class);

  public ProjectOverviewGrid() {
    setEditorEnabled(true);
    setSelectionMode(SelectionMode.SINGLE);
    setHeightMode(HeightMode.ROW);
    setHeightByRows(5d);
    setSizeFull();

  }

  @Override
  public void saveEditor() throws FieldGroup.CommitException {
    super.saveEditor();
    refreshVisibleRows();
    isChanged.setValue(!isChanged.getValue());
  }

  /**
   * We need to refresh the rows manually after saving
   */
  public void refreshVisibleRows() {
    Collection<Extension> extensions = getExtensions();
    extensions.stream().filter(extension -> extension instanceof RpcDataProviderExtension)
        .forEach(extension -> {
          ((RpcDataProviderExtension) extension).refreshCache();
        });
  }

}
