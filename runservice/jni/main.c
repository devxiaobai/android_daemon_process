#include "runservice.h"
#include <signal.h>
#include <getopt.h>
#include <errno.h>
#include <stdlib.h>

int main( int argc, char* argv[]  )  
{  
  signal(SIGTERM, SIG_IGN);
  
  const char *process_name = NULL;
  const char *package_name = NULL;
  const char *activity_name = NULL;
  int interval_sec = 30;
  
  struct option options[] =
	{
	{ "process_name", required_argument, 0, 'p' },
	{ "package_name", required_argument, 0, 'a' },
	{ "activity_name", required_argument, 0, 'c' },
	{ "interval_sec", required_argument, 0, 'i' },
	{ 0, 0, 0, 0 }
	};

	int c;

	for (;;)
	{
		c = getopt_long(argc, argv, "p:a:c:i:", options, NULL);
		if (c == -1)
		{
			break;
		}
		switch (c)
		{
		case 'p':
			process_name = optarg;
			break;
		case 'a':
			package_name = optarg;
			break;
		case 'c':
			activity_name = optarg;
			break;
		case 'i':
			interval_sec = atoi(optarg);
			break;
		default:
			exit(EXIT_FAILURE);
		}
	}
	
	if (process_name == NULL || package_name == NULL || activity_name == NULL)
		exit(EXIT_FAILURE);
    
   setsid();
  daemon(1, 1);
  
  run_service(process_name, package_name, activity_name, 10);
    
  return 0;
}
