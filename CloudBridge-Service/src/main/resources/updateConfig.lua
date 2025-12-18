-- KEYS[1] = 配置 key (config:dataId:group)
-- KEYS[2] = 版本号 key (config_ver:dataId:group)
-- ARGV[1] = 当前版本（期望版本）
-- ARGV[2] = 新配置内容
-- ARGV[3] = dataId
-- ARGV[4] = group

local current_version = tonumber(redis.call("GET", KEYS[2])) or 0

if current_version == tonumber(ARGV[1]) then
    redis.call("SET", KEYS[1], ARGV[2])
    redis.call("INCR", KEYS[2])
    -- 推送到 Redis Stream 中
    redis.call("XADD", "config:stream", "*",
        "uri", ARGV[3],
        "name", ARGV[4],
        "version", current_version + 1)
    return { "OK", current_version + 1 }
else
    local current_value = redis.call("GET", KEYS[1])
    return { "CONFLICT", current_version }
end