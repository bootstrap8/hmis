package com.github.hbq.common.utils;

import java.util.List;
import lombok.Data;

/**
 * @author hbq
 */
@Data
public class SubList<T>
{

    private Offset offset;
    private List<T> list;
}
