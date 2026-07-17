package com.example.carduino.shared;

import java.util.ArrayList;
import java.util.List;

public interface BaseEnum {
    Integer getId();
    default List<PayloadType> getPayloadTypes(){return new ArrayList<>();};
}
