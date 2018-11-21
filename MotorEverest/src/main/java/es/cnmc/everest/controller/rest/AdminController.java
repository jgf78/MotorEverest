package es.cnmc.everest.controller.rest;

import es.cnmc.component.service.rest.AbstractAdminController;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


@RestController
@RequestMapping(value = "/admin", produces = MediaType.APPLICATION_JSON_VALUE)
public class AdminController extends AbstractAdminController {

    public AdminController() {
        super();
        this.cache = false;
        this.databaseEnable = false;
    }

}
