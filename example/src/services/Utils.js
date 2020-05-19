export const wait = (time = 1000) => {
  return new Promise(resolve => {
    setTimeout(() => {
      resolve();
    }, time);
  });
};
