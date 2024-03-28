package org.nopware.jwt_util;

import lombok.Builder;
import lombok.Value;
import lombok.extern.jackson.Jacksonized;

@Value
@Builder
@Jacksonized
public class Header {
    String alg;
    String typ;
}
