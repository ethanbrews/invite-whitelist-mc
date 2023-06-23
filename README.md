# Whitelist Invite Mod

*Fabric Mod*

This mod adds the `/invite` command to invite players, adding them to the whitelist (without op privileges).

The invite can be revoked within a set amount of time (default: 10 minutes) by using the `/revoke` command, after which
whitelist permissions can be revoked using the existing `/whitelist` command.

Established (but non-op) players may have their access revoked if enough players vote to revoke their access using `/revoke`.

## Commands

### `/invite <username>`

Whitelist a player with the given username.

- Access will be revoked if the player doesn't join within the InviteTimeout.
- Access can be revoked immediately by the player using `/revoke <username>` within the RevokeTimeout.
- The revoke command will still work, even if it's disabled, within the RevokeTimeout.

### `/revoke <username>`

Revoke whitelist access for a player, or vote to revoke access for a player.

- If the player executing the command both invited the player and the RevokeTimeout hasn't expired, access is 
immediately revoked.
- Otherwise, the revoke is recorded as a vote. 
- If the number of votes to revoke access for a given player reaches or exceeds the RevokeMajority, access is revoked.
- If the player is in the RevokeBlacklist or is an op, the player will never have their whitelist access revoked.
- If RevokeEnabled is false, the vote system is disabled.

## Configuration

| Option          | Type       | Meaning                                                                                                      | Default |
|-----------------|------------|--------------------------------------------------------------------------------------------------------------|---------|
| InviteTimeout   | Time       | Time before an invite becomes invalid if the invited player doesn't join the server                          | 10 Min  |
| RevokeEnabled   | Bool       | Is the revoke command enabled? **The revoke command can still be used within the RevokeTimeout limit!**      | True    |
| RevokeTimeout   | Time       | Time during which a player's access can be immediately revoked without a vote.                               | 10 Min  |
| RevokeMajority  | Int        | The number of players that must anonymously vote to revoke a player from the whitelist. Can be a percentage. | 50%     |
| CanRevokeOp     | Bool       | True if op players can have access revoked via a /revoke vote                                                | False   |
| RevokeBlacklist | List<UUID> | List of UUIDs for players that are protected from /revoke votes                                              | []      |