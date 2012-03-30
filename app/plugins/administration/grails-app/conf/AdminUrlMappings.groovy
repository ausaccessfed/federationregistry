class AdminUrlMappings {

  static mappings = {
    // Dash
    "/administration/dashboard"{
      controller="adminDashboard"
      action="index"
    }

    // User administration
    "/administration/subjects/$action?/$id?"{
      controller = "subject" 
    }

    "/administration/roles/$action?/$id?"{
      controller = "role" 
    }

    // Data Management
    "/administration/attributes/$action?/$id?"{
      controller = "attributeBase"
    }

    "/administration/attributecategory/$action?/$id?"{
      controller = "attributeCategory"
    }

    "/administration/monitortype/$action?/$id?"{
      controller = "monitorType"
    }

    "/administration/organisationtype/$action?/$id?"{
      controller = "organizationType"
    }

    "/administration/contacttype/$action?/$id?"{
      controller = "contactType"
    }

    "/administration/cakeyinfo/$action?/$id?"{
      controller = "CAKeyInfo"
    }

   "/administration/samluri/$action?/$id?"{
      controller = "samlURI" 
    }

    "/administration/console/$action?/$id?"{
      controller = "adminConsole"
    }
  
    "/console/$action?/$id?"{
      controller = "console"
    }
  }

}