package org.cardvault.userPacks.data;

import java.sql.Timestamp;

public record UserPackDOM(int id,
                          int userId,
                          int packTypeId,
                          int quantity) {
}
