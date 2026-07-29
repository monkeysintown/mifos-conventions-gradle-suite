///
/// This Source Code Form is subject to the terms of the Mozilla Public
/// License, v. 2.0. If a copy of the MPL was not distributed with this
/// file, You can obtain one at http://mozilla.org/MPL/2.0/.
///
package org.mifos.conventions.gradle.workflow.generator.example;

import java.util.Optional;
import org.mifos.conventions.gradle.workflow.generator.example.models.CurrencyConfigurationData;
import org.mifos.conventions.gradle.workflow.generator.example.models.CurrencyUpdateRequest;
import org.mifos.conventions.gradle.workflow.generator.example.models.CurrencyUpdateResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;

public interface CurrencyApi {

    ResponseEntity<CurrencyConfigurationData> retrieveCurrencies();

    ResponseEntity<CurrencyUpdateResponse> updateCurrencies(
            @RequestBody(required = false) Optional<CurrencyUpdateRequest> currencyUpdateRequest);
}
