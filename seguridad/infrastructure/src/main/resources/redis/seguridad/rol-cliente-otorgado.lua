local generacion = redis.call('GET', KEYS[1])
if not generacion then
    return -1
end

local clave = string.format(ARGV[1], generacion) .. ARGV[2]
local pertenencia = redis.call('SMISMEMBER', clave, unpack(ARGV, 3))

for _, valor in ipairs(pertenencia) do
    if valor == 1 then
        return 1
    end
end
return 0
