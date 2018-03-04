package ru.iorcontrol.ior.ior;

import java.util.List;

import ru.iorcontrol.ior.ior.Model.FieldTemplate;

/**
 * Created by alexeykazinets on 30/11/2017.
 */

public class StatusTemplate {

    String name;
    Boolean isFinal;
    List<FieldTemplate> fields;
    List<String> users_permission_to_edit;
    List<String> groups_permission_to_edit;

    public StatusTemplate(String name, Boolean isFinal, List<FieldTemplate> fields, List<String> users_permission_to_edit, List<String> groups_permission_to_edit) {
        this.name = name;
        this.isFinal = isFinal;
        this.fields = fields;
        this.users_permission_to_edit = users_permission_to_edit;
        this.groups_permission_to_edit = groups_permission_to_edit;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Boolean getFinal() {
        return isFinal;
    }

    public void setFinal(Boolean aFinal) {
        isFinal = aFinal;
    }

    public List<FieldTemplate> getFields() {
        return fields;
    }

    public void setFields(List<FieldTemplate> fields) {
        this.fields = fields;
    }

    public List<String> getUsers_permission_to_edit() {
        return users_permission_to_edit;
    }

    public void setUsers_permission_to_edit(List<String> users_permission_to_edit) {
        this.users_permission_to_edit = users_permission_to_edit;
    }

    public List<String> getGroups_permission_to_edit() {
        return groups_permission_to_edit;
    }

    public void setGroups_permission_to_edit(List<String> groups_permission_to_edit) {
        this.groups_permission_to_edit = groups_permission_to_edit;
    }
}
