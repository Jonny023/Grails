package grails.sso

class BootStrap {

    def userService

    def init = { servletContext ->

        // init default data
        userService.initData()
    }

    def destroy = {
    }
}
