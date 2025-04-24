local auth_header = ngx.var.http_Authorization
if not auth_header then
    ngx.status = 401
    ngx.say("Missing Authorization header")
    ngx.exit(401)
end

local _, _, token = string.find(auth_header, "Bearer%s+(.+)")
if not token then
    ngx.status = 401
    ngx.say("Invalid Authorization header")
    ngx.exit(401)
end

local jwt = require "resty.jwt"
local secret = ngx.shared.secrets:get("jwt_secret")
if not secret then
    ngx.status = 500
    ngx.say("JWT secret not configured")
    ngx.exit(500)
end

local jwt_obj = jwt:verify(secret, token)
if not jwt_obj.verified then
    ngx.status = 401
    ngx.say("Invalid token")
    ngx.exit(401)
end

-- Берём claim 'id'
local user_id = jwt_obj.payload.id
if not user_id or user_id == "" then
    ngx.status = 401
    ngx.say("No 'id' claim in token")
    ngx.exit(401)
end

-- Ставим в переменную для proxy_set_header
ngx.var.jwt_userid = user_id

-- Удаляем Authorization заголовок
ngx.req.clear_header("Authorization")