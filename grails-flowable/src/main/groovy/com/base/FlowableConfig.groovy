package com.base

import org.flowable.spring.SpringProcessEngineConfiguration
import org.flowable.spring.boot.EngineConfigurationConfigurer
import org.springframework.context.annotation.Configuration

/**
 *  查看流程图中文乱码
 * @Author Lee* @Description
 * @Date 2019年03月23日 23:43
 *
 */
@Configuration
class FlowableConfig implements EngineConfigurationConfigurer<SpringProcessEngineConfiguration> {

    @Override
    void configure(SpringProcessEngineConfiguration engineConfiguration) {
        engineConfiguration.setActivityFontName("宋体")
        engineConfiguration.setLabelFontName("宋体")
        engineConfiguration.setAnnotationFontName("宋体")
    }
}
