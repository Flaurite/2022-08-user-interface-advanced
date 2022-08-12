package com.company.jmixpm.screen.city;

import io.jmix.ui.screen.*;
import com.company.jmixpm.entity.City;

@UiController("City.edit")
@UiDescriptor("city-edit.xml")
@EditedEntityContainer("cityDc")
public class CityEdit extends StandardEditor<City> {
}