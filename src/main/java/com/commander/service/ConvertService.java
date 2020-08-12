package com.commander.service;

import com.commander.model.Convertible;

public interface ConvertService {

    void convert(Convertible convertible) throws Exception;
    void onClose();


}
