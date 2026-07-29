///
/// This Source Code Form is subject to the terms of the Mozilla Public
/// License, v. 2.0. If a copy of the MPL was not distributed with this
/// file, You can obtain one at http://mozilla.org/MPL/2.0/.
///
package org.mifos.conventions.gradle.workflow.generator.example.models;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotNull;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class CurrencyUpdateRequest implements Serializable {

    private static final long serialVersionUID = 1L;

    private List<String> currencies;

    public CurrencyUpdateRequest() {
        super();
    }

    public CurrencyUpdateRequest(List<String> currencies) {
        this.currencies = currencies;
    }

    public CurrencyUpdateRequest currencies(List<String> currencies) {
        this.currencies = currencies;
        return this;
    }

    public CurrencyUpdateRequest addCurrenciesItem(String currenciesItem) {
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
    @NotNull @JsonProperty("currencies")
    public List<String> getCurrencies() {
        return currencies;
    }

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
        CurrencyUpdateRequest currencyUpdateRequest = (CurrencyUpdateRequest) o;
        return Objects.equals(this.currencies, currencyUpdateRequest.currencies);
    }

    @Override
    public int hashCode() {
        return Objects.hash(currencies);
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("class CurrencyUpdateRequest {\n");
        sb.append("    currencies: ").append(toIndentedString(currencies)).append("\n");
        sb.append("}");
        return sb.toString();
    }

    /** Convert the given object to string with each line indented by 4 spaces (except the first line). */
    private String toIndentedString(Object o) {
        return o == null ? "null" : o.toString().replace("\n", "\n    ");
    }
}
