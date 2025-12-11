package com.mpx.minipx.framework.config;

//✅ import 주의!
import org.springframework.boot.autoconfigure.data.redis.LettuceClientConfigurationBuilderCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import io.lettuce.core.ClientOptions;
import io.lettuce.core.protocol.ProtocolVersion;

@Configuration
public class LettuceTweaksConfig {

// @Bean
// LettuceClientConfigurationBuilderCustomizer forceResp2() {
//     return builder -> builder.clientOptions(
//         ClientOptions.builder()
//             .protocolVersion(ProtocolVersion.RESP2) // HELLO 없이 AUTH 경로로
//             .build()
//     );
// }
}