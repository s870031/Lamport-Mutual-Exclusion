import os

def readFileList(rootDir,pathSet):
    for lists in os.listdir(rootDir):
        path = os.path.join(rootDir, lists)
        #print path
        pathSet.append(path)
        if os.path.isdir(path):
            readFileList(path,pathSet)


def main():
    fileDir = []
    EnterTimeStamps = []
    readFileList(os.getcwd(),fileDir)
    #EnterTimeStamps.append(102)
    for dir in fileDir:
	if	(dir[-12:-6] == "config") and (dir[-3:] == "out"):
            f = open(dir)
            lines = f.readlines()
            for index in range(len(lines)/2):
                EnterTimeStamps.append(int(lines[index*2].split(' ')[1]))
    
    test = list(set(EnterTimeStamps))
    if (len(EnterTimeStamps) == len(test)):
       print "Pass Test!\nBecause Time to Enter Critical Section are Different"
    else:
		print "Pass Failed"
                

if __name__ == '__main__':
    main()
