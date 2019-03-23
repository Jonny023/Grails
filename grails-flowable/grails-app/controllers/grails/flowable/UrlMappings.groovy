package grails.flowable

class UrlMappings {

    static mappings = {
        "/$controller/$action?/$id?(.$format)?" {
            constraints {
                // apply constraints here
            }
        }

        get "/$controller/$id(.$format)?/json"(action: "get")
        put "/$controller/$id(.$format)?/save"(action: "save")
//        get  "/$controller/editor/getMenu"(action: "getMenu")
        post "/$controller/create"(action: "newModel")
        "/$controller/modelList"(action: "modelList")
        get "/$controller/deploy"(action: "deploy")
        get "/$controller/start"(action: "startProcess")

        "/"(controller: "flow", action: "list")
        "500"(view: '/error')
        "404"(view: '/notFound')
    }
}
