package com.commander.model;

import org.springframework.stereotype.Component;

@Component
@FunctionalInterface
public interface Convertible {
    void convert();
}
