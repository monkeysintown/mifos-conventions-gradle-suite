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
import java.util.Objects;

public class CurrencyItem implements Serializable {

    private static final long serialVersionUID = 1L;

    @JsonInclude(JsonInclude.Include.NON_NULL)
    private String code;

    @JsonInclude(JsonInclude.Include.NON_NULL)
    private Integer decimalPlaces;

    @JsonInclude(JsonInclude.Include.NON_NULL)
    private String displayLabel;

    @JsonInclude(JsonInclude.Include.NON_NULL)
    private String displaySymbol;

    @JsonInclude(JsonInclude.Include.NON_NULL)
    private Integer inMultiplesOf;

    @JsonInclude(JsonInclude.Include.NON_NULL)
    private String name;

    @JsonInclude(JsonInclude.Include.NON_NULL)
    private String nameCode;

    public CurrencyItem code(String code) {
        this.code = code;
        return this;
    }

    /**
     * Get code
     *
     * @return code
     */
    @JsonProperty("code")
    public String getCode() {
        return code;
    }

    @JsonSetter(nulls = Nulls.SKIP)
    @JsonProperty("code")
    public void setCode(String code) {
        this.code = code;
    }

    public CurrencyItem decimalPlaces(Integer decimalPlaces) {
        this.decimalPlaces = decimalPlaces;
        return this;
    }

    /**
     * Get decimalPlaces
     *
     * @return decimalPlaces
     */
    @JsonProperty("decimalPlaces")
    public Integer getDecimalPlaces() {
        return decimalPlaces;
    }

    @JsonSetter(nulls = Nulls.SKIP)
    @JsonProperty("decimalPlaces")
    public void setDecimalPlaces(Integer decimalPlaces) {
        this.decimalPlaces = decimalPlaces;
    }

    public CurrencyItem displayLabel(String displayLabel) {
        this.displayLabel = displayLabel;
        return this;
    }

    /**
     * Get displayLabel
     *
     * @return displayLabel
     */
    @JsonProperty("displayLabel")
    public String getDisplayLabel() {
        return displayLabel;
    }

    @JsonSetter(nulls = Nulls.SKIP)
    @JsonProperty("displayLabel")
    public void setDisplayLabel(String displayLabel) {
        this.displayLabel = displayLabel;
    }

    public CurrencyItem displaySymbol(String displaySymbol) {
        this.displaySymbol = displaySymbol;
        return this;
    }

    /**
     * Get displaySymbol
     *
     * @return displaySymbol
     */
    @JsonProperty("displaySymbol")
    public String getDisplaySymbol() {
        return displaySymbol;
    }

    @JsonSetter(nulls = Nulls.SKIP)
    @JsonProperty("displaySymbol")
    public void setDisplaySymbol(String displaySymbol) {
        this.displaySymbol = displaySymbol;
    }

    public CurrencyItem inMultiplesOf(Integer inMultiplesOf) {
        this.inMultiplesOf = inMultiplesOf;
        return this;
    }

    /**
     * Get inMultiplesOf
     *
     * @return inMultiplesOf
     */
    @JsonProperty("inMultiplesOf")
    public Integer getInMultiplesOf() {
        return inMultiplesOf;
    }

    @JsonSetter(nulls = Nulls.SKIP)
    @JsonProperty("inMultiplesOf")
    public void setInMultiplesOf(Integer inMultiplesOf) {
        this.inMultiplesOf = inMultiplesOf;
    }

    public CurrencyItem name(String name) {
        this.name = name;
        return this;
    }

    /**
     * Get name
     *
     * @return name
     */
    @JsonProperty("name")
    public String getName() {
        return name;
    }

    @JsonSetter(nulls = Nulls.SKIP)
    @JsonProperty("name")
    public void setName(String name) {
        this.name = name;
    }

    public CurrencyItem nameCode(String nameCode) {
        this.nameCode = nameCode;
        return this;
    }

    /**
     * Get nameCode
     *
     * @return nameCode
     */
    @JsonProperty("nameCode")
    public String getNameCode() {
        return nameCode;
    }

    @JsonSetter(nulls = Nulls.SKIP)
    @JsonProperty("nameCode")
    public void setNameCode(String nameCode) {
        this.nameCode = nameCode;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        CurrencyItem currencyItem = (CurrencyItem) o;
        return Objects.equals(this.code, currencyItem.code)
                && Objects.equals(this.decimalPlaces, currencyItem.decimalPlaces)
                && Objects.equals(this.displayLabel, currencyItem.displayLabel)
                && Objects.equals(this.displaySymbol, currencyItem.displaySymbol)
                && Objects.equals(this.inMultiplesOf, currencyItem.inMultiplesOf)
                && Objects.equals(this.name, currencyItem.name)
                && Objects.equals(this.nameCode, currencyItem.nameCode);
    }

    @Override
    public int hashCode() {
        return Objects.hash(code, decimalPlaces, displayLabel, displaySymbol, inMultiplesOf, name, nameCode);
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("class CurrencyItem {\n");
        sb.append("    code: ").append(toIndentedString(code)).append("\n");
        sb.append("    decimalPlaces: ").append(toIndentedString(decimalPlaces)).append("\n");
        sb.append("    displayLabel: ").append(toIndentedString(displayLabel)).append("\n");
        sb.append("    displaySymbol: ").append(toIndentedString(displaySymbol)).append("\n");
        sb.append("    inMultiplesOf: ").append(toIndentedString(inMultiplesOf)).append("\n");
        sb.append("    name: ").append(toIndentedString(name)).append("\n");
        sb.append("    nameCode: ").append(toIndentedString(nameCode)).append("\n");
        sb.append("}");
        return sb.toString();
    }

    /** Convert the given object to string with each line indented by 4 spaces (except the first line). */
    private String toIndentedString(Object o) {
        return o == null ? "null" : o.toString().replace("\n", "\n    ");
    }
}
