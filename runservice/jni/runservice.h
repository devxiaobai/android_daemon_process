#ifdef __cplusplus
extern "C" {
#endif

extern int chk_process(const char *process_name);

extern void run_service(const char *process_name, const char *package_name, const char *activity_name, int interval_sec);

#ifdef __cplusplus
}
#endif
