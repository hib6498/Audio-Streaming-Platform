from config import WorkerConfig, build_s3_client

config = WorkerConfig.from_env()
client = build_s3_client(config)

# print([x for x in dir(client) if not x.startswith("_")])
# help(client.delete_objects)
print(*client.list_objects(Bucket="music-store")["Contents"], sep="\n")

# objs = client.list_objects(Bucket="music-store")["Contents"]
# no_del = {"252/1. I Just Might.opus", "253/1. Youngblood.opus", "tracks/402/audio.ogg"}

# to_delete = [{"Key": obj["Key"]} for obj in objs if obj["Key"] not in no_del]
# response = client.delete_objects(Bucket="music-store", Delete={"Objects": to_delete})
# print(response)
