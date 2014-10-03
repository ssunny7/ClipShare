#ifndef CLIP_SHARE_SERVER_LISTENER_HEADER
#define	CLIP_SHARE_SERVER_LISTENER_HEADER

#include "Logger.h"
#include <Windows.h>

class ClipShareServerListener
{
	private:
		HANDLE hServerListenerThread, hServerListenerWorkerThread;
		SOCKET sockServer;
		Logger logger;

		DWORD InitServerListenerWorker();
		bool ProcessClient(SOCKET *);

		static DWORD WINAPI ServerListenerThread(LPVOID);
		static DWORD WINAPI ServerListenerWorkerThread(LPVOID);

		static const int SERVER_LISTENER_PORT = 7489;

		HANDLE hServiceStopEvt;
		bool bServiceStopping;

	public:
		ClipShareServerListener(Logger *);

		DWORD InitServerListener();
		void CleanServerListener();
		
		DWORD CSServerListenerThread(LPVOID);
		DWORD CSServerListenerWorkerThread(LPVOID);

		void SetServiceStopEvt();
};

#endif
