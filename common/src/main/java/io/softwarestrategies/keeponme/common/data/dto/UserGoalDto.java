package io.softwarestrategies.keeponme.common.data.dto;

import io.softwarestrategies.keeponme.common.data.enumeration.UserGoalStatus;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class UserGoalDto {

    private Integer id;

    @Size(min = 5, max = 50, message = "Name must be between 5 and 50 characters")
    private String name;

    @Size(min = 10, max = 200, message = "Description must be between 10 and 255 characters")
    private String description;

    @NotEmpty
    private UserGoalStatus status;

    public UserGoalDto() {}

    public UserGoalDto(Integer id, String name) {
        this.id = id;
        this.name = name;
        this.status = UserGoalStatus.NEW;
    }
}
