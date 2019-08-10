package interceptor

import grails.testing.web.interceptor.InterceptorUnitTest
import spock.lang.Specification

class SSOInterceptorSpec extends Specification implements InterceptorUnitTest<SSOInterceptor> {

    def setup() {
    }

    def cleanup() {

    }

    void "Test SSO interceptor matching"() {
        when:"A request matches the interceptor"
            withRequest(controller:"SSO")

        then:"The interceptor does match"
            interceptor.doesMatch()
    }
}
