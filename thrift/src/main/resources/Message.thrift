namespace java com.github.herong.thrift.comm

struct DTO {
    1.
	1:map<string,string> datas
}

service  {
	DTO execute(1:DTO dto)
}