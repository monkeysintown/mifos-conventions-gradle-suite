///
/// This Source Code Form is subject to the terms of the Mozilla Public
/// License, v. 2.0. If a copy of the MPL was not distributed with this
/// file, You can obtain one at http://mozilla.org/MPL/2.0/.
///
package org.mifos.conventions.gradle.workflow.generator.example.models;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonSetter;
import com.fasterxml.jackson.annotation.Nulls;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class CurrencyConfigurationData implements Serializable {

    private static final long serialVersionUID = 1L;

    @JsonInclude(JsonInclude.Include.NON_NULL)
    private List<CurrencyData> currencyOptions;

    @JsonInclude(JsonInclude.Include.NON_NULL)
    private List<CurrencyData> selectedCurrencyOptions;

    public CurrencyConfigurationData currencyOptions(List<CurrencyData> currencyOptions) {
        this.currencyOptions = currencyOptions;
        return this;
    }

    public CurrencyConfigurationData addCurrencyOptionsItem(CurrencyData currencyOptionsItem) {
        if (this.currencyOptions == null) {
            this.currencyOptions = new ArrayList<>();
        }
        this.currencyOptions.add(currencyOptionsItem);
        return this;
    }

    /**
     * Get currencyOptions
     *
     * @return currencyOptions
     */
    @JsonProperty("currencyOptions")
    public List<CurrencyData> getCurrencyOptions() {
        return currencyOptions;
    }

    @JsonSetter(nulls = Nulls.SKIP)
    @JsonProperty("currencyOptions")
    public void setCurrencyOptions(List<CurrencyData> currencyOptions) {
        this.currencyOptions = currencyOptions;
    }

    public CurrencyConfigurationData selectedCurrencyOptions(List<CurrencyData> selectedCurrencyOptions) {
        this.selectedCurrencyOptions = selectedCurrencyOptions;
        return this;
    }

    public CurrencyConfigurationData addSelectedCurrencyOptionsItem(CurrencyData selectedCurrencyOptionsItem) {
        if (this.selectedCurrencyOptions == null) {
            this.selectedCurrencyOptions = new ArrayList<>();
        }
        this.selectedCurrencyOptions.add(selectedCurrencyOptionsItem);
        return this;
    }

    /**
     * Get selectedCurrencyOptions
     *
     * @return selectedCurrencyOptions
     */
    @JsonProperty("selectedCurrencyOptions")
    public List<CurrencyData> getSelectedCurrencyOptions() {
        return selectedCurrencyOptions;
    }

    @JsonSetter(nulls = Nulls.SKIP)
    @JsonProperty("selectedCurrencyOptions")
    public void setSelectedCurrencyOptions(List<CurrencyData> selectedCurrencyOptions) {
        this.selectedCurrencyOptions = selectedCurrencyOptions;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        CurrencyConfigurationData currencyConfigurationData = (CurrencyConfigurationData) o;
        return Objects.equals(this.currencyOptions, currencyConfigurationData.currencyOptions)
                && Objects.equals(this.selectedCurrencyOptions, currencyConfigurationData.selectedCurrencyOptions);
    }

    @Override
    public int hashCode() {
        return Objects.hash(currencyOptions, selectedCurrencyOptions);
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("class CurrencyConfigurationData {\n");
        sb.append("    currencyOptions: ")
                .append(toIndentedString(currencyOptions))
                .append("\n");
        sb.append("    selectedCurrencyOptions: ")
                .append(toIndentedString(selectedCurrencyOptions))
                .append("\n");
        sb.append("}");
        return sb.toString();
    }

    /** Convert the given object to string with each line indented by 4 spaces (except the first line). */
    private String toIndentedString(Object o) {
        return o == null ? "null" : o.toString().replace("\n", "\n    ");
    }
}
