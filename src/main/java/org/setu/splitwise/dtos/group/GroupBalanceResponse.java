package org.setu.splitwise.dtos.group;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class GroupBalanceResponse {

    private String groupId;
    private List<UserBalance> userBalances;
}
