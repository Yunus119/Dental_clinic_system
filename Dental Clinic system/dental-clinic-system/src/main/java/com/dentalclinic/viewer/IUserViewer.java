package com.dentalclinic.viewer;

import java.util.List;
import com.dentalclinic.model.User;

public interface IUserViewer {

    // search doctor
    List<User> searchDoctor(String name);
}