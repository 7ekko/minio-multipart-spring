package top.mind.miniomultipartspringstarter.domain.param;

import lombok.Data;

import javax.validation.constraints.NotNull;

@Data
public class RecycleOrRecoveryVM {
    @NotNull
    private String fileName;
    @NotNull
    private Integer isRecycle;
}
