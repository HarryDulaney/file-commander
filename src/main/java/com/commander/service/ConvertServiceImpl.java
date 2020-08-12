package com.commander.service;

import com.commander.model.Convertible;
import org.springframework.stereotype.Service;

@Service("convertService")
public class ConvertServiceImpl extends ParentService implements ConvertService {


    @Override
    public void convert(Convertible convertible) {
        convertible.convert();
    }

}
