package com.example.backend.pojo;


import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
@TableName("permission")
public class Permission implements Serializable {
    @TableId(type = IdType.AUTO)
    private Long id;
    private String permissionName;
    private String description;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
