local auth_header = ngx.var.http_Authorization

if not auth_header then
    ngx.log(ngx.ERR, "Missing Authorization header")
    ngx.status = 401
    ngx.say("Missing Authorization header")
    ngx.exit(401)
end

local _, _, token = string.find(auth_header, "Bearer%s+(.+)")

if not token then
    ngx.log(ngx.ERR, "Invalid Authorization header format")
    ngx.status = 401
    ngx.say("Invalid Authorization header")
    ngx.exit(401)
end

local jwt = require "resty.jwt"
local secret = ngx.shared.secrets:get("jwt_secret")

if not secret then
    ngx.log(ngx.ERR, "JWT secret not configured")
    ngx.status = 500
    ngx.say("JWT secret not configured")
    ngx.exit(500)
end

local jwt_obj = jwt:verify(secret, token)
if not jwt_obj.verified then
    ngx.log(ngx.ERR, "Invalid token: ", tostring(jwt_obj.reason))
    ngx.status = 401
    ngx.say("Invalid token")
    ngx.exit(401)
end

local user_id = jwt_obj.payload.id
local group_name = jwt_obj.payload.groupName

if not user_id or user_id == "" then
    ngx.log(ngx.ERR, "No 'id' claim in token")
    ngx.status = 401
    ngx.say("No 'id' claim in token")
    ngx.exit(401)
end

if not group_name or group_name == "" then
    ngx.log(ngx.ERR, "No 'groupName' claim in token")
    ngx.status = 401
    ngx.say("No 'groupName' claim in token")
    ngx.exit(401)
end

ngx.var.jwt_userid = user_id
ngx.var.jwt_groupname = group_name

-
ngx.req.clear_header("Authorization")
