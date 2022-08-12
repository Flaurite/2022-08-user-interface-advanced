package com.company.jmixpm.screen.filterfragment;

import io.jmix.core.querycondition.PropertyConditionUtils;
import io.jmix.ui.UiComponents;
import io.jmix.ui.component.GroupBoxLayout;
import io.jmix.ui.component.PropertyFilter;
import io.jmix.ui.model.CollectionLoader;
import io.jmix.ui.screen.ScreenFragment;
import io.jmix.ui.screen.Subscribe;
import io.jmix.ui.screen.UiController;
import io.jmix.ui.screen.UiDescriptor;
import org.springframework.beans.factory.annotation.Autowired;

@UiController("FilterFragment")
@UiDescriptor("filter-fragment.xml")
public class FilterFragment extends ScreenFragment {

    @Autowired
    private GroupBoxLayout filterBox;

    @Autowired
    private UiComponents uiComponents;

    private String property;
    private CollectionLoader<?> dataLoader;

    public void setProperty(String property) {
        this.property = property;
    }

    public void setDataLoader(CollectionLoader<?> dataLoader) {
        this.dataLoader = dataLoader;
    }

    @Subscribe
    public void onAttach(AttachEvent event) {
        if (dataLoader == null || property == null) {
            throw new IllegalStateException("dataLoader and property cannot be null");
        }
        initFilter(dataLoader, property, PropertyFilter.Operation.CONTAINS);
    }

    private void initFilter(CollectionLoader<?> dataLoader, String property, PropertyFilter.Operation operation) {
        PropertyFilter<?> filter = uiComponents.create(PropertyFilter.class);
        filter.setDataLoader(dataLoader);
        filter.setProperty(property);
        filter.setOperation(operation);
        filter.setParameterName(PropertyConditionUtils.generateParameterName(property));
        filter.setOperationCaptionVisible(false);

        filterBox.add(filter);
    }
}