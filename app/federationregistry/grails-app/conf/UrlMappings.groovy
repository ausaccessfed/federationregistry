class UrlMappings {

  static mappings = {        
    "/"{
      controller = "welcome"
    }

    "/dashboard/$action?/$id?"{
      controller = "dashboard"
    }

    "/bootstrap/$action?/$id?" {
      controller = "initialBootstrap"
    }

    "403"(view:'/403')
    "404"(view:'/404')
    "500"(view:'/500')

    // Development only.
    "/greenmail/$action?/$id?"{
      controller = "greenmail"
    }
  }

}
