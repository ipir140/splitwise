package org.setu.splitwise.models;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.setu.splitwise.Utils.JsonUtils;
import org.springframework.util.StringUtils;

import javax.persistence.*;
import java.util.Collections;
import java.util.Map;
import java.util.Set;

@Entity
@Table(name = "group_data")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Group {

    @Id
    @Column(name = "PR_KEY", unique = true)
    private String id;

    private String userIdsString;

    public void setUserIds(Set<Long> userIds) {
        try {
            this.userIdsString = JsonUtils.objectMapper.writeValueAsString(userIds);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }

    public Set<Long> getUserIds() {
        try {
            return JsonUtils.objectMapper.readValue(userIdsString, new TypeReference<Set<Long>>() {});
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }
}
