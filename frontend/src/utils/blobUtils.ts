// Source - https://stackoverflow.com/a/29390393
// Posted by Chris Barr, modified by community. See post 'Timeline' for change history
// Retrieved 2026-04-03, License - CC BY-SA 4.0

export const blobToFile = (theBlob: Blob, fileName: string): File => {
  return new File([theBlob], fileName, { type: theBlob.type });
};
