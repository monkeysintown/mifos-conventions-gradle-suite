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
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

public class CurrencyUpdateResponse implements Serializable {

    private static final long serialVersionUID = 1L;

    @JsonInclude(JsonInclude.Include.NON_NULL)
    private Map<String, Object> changes;

    @JsonInclude(JsonInclude.Include.NON_NULL)
    private List<String> currencies;

    public CurrencyUpdateResponse changes(Map<String, Object> changes) {
        this.changes = changes;
        return this;
    }

    public CurrencyUpdateResponse putChangesItem(String key, Object changesItem) {
        if (this.changes == null) {
            this.changes = new HashMap<>();
        }
        this.changes.put(key, changesItem);
        return this;
    }

    /**
     * Get changes
     *
     * @return changes
     */
    @JsonProperty("changes")
    public Map<String, Object> getChanges() {
        return changes;
    }

    @JsonSetter(nulls = Nulls.SKIP)
    @JsonProperty("changes")
    public void setChanges(Map<String, Object> changes) {
        this.changes = changes;
    }

    public CurrencyUpdateResponse currencies(List<String> currencies) {
        this.currencies = currencies;
        return this;
    }

    public CurrencyUpdateResponse addCurrenciesItem(String currenciesItem) {
        if (this.currencies == null) {
            this.currencies = new ArrayList<>();
        }
        this.currencies.add(currenciesItem);
        return this;
    }

    /**
     * Get currencies
     *
     * @return currencies
     */
    @JsonProperty("currencies")
    public List<String> getCurrencies() {
        return currencies;
    }

    @JsonSetter(nulls = Nulls.SKIP)
    @JsonProperty("currencies")
    public void setCurrencies(List<String> currencies) {
        this.currencies = currencies;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        CurrencyUpdateResponse currencyUpdateResponse = (CurrencyUpdateResponse) o;
        return Objects.equals(this.changes, currencyUpdateResponse.changes)
                && Objects.equals(this.currencies, currencyUpdateResponse.currencies);
    }

    @Override
    public int hashCode() {
        return Objects.hash(changes, currencies);
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("class CurrencyUpdateResponse {\n");
        sb.append("    changes: ").append(toIndentedString(changes)).append("\n");
        sb.append("    currencies: ").append(toIndentedString(currencies)).append("\n");
        sb.append("}");
        return sb.toString();
    }

    /** Convert the given object to string with each line indented by 4 spaces (except the first line). */
    private String toIndentedString(Object o) {
        return o == null ? "null" : o.toString().replace("\n", "\n    ");
    }
}
