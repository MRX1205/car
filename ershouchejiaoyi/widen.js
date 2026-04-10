const fs = require('fs');
const path = require('path');

function walkDir(dir, callback) {
  fs.readdirSync(dir).forEach(f => {
    let dirPath = path.join(dir, f);
    let isDirectory = fs.statSync(dirPath).isDirectory();
    isDirectory ? walkDir(dirPath, callback) : callback(dirPath);
  });
}

const vueDir = '/Users/lyhm/Study/Projects/ershouchejiaoyi/front/src';

walkDir(vueDir, function(filePath) {
  if (filePath.endsWith('.vue') && !filePath.includes('Home.vue')) {
    let content = fs.readFileSync(filePath, 'utf8');
    let changed = false;

    // Apply layout widening
    if (content.includes('max-width: 1200px;')) {
      content = content.replace(/max-width:\s*1200px;/g, 'max-width: 1600px;\n  width: 96%;');
      changed = true;
    }
    if (content.includes('max-width: 1000px;')) {
      content = content.replace(/max-width:\s*1000px;/g, 'max-width: 1400px;\n  width: 92%;');
      changed = true;
    }
    if (content.includes('max-width: 900px;')) {
      content = content.replace(/max-width:\s*900px;/g, 'max-width: 1200px;\n  width: 90%;');
      changed = true;
    }
    if (content.includes('max-width: 800px;')) {
      // Login and user forms filter
      if (!filePath.includes('Login.vue') && !filePath.includes('Register.vue') && !filePath.includes('VehicleForm.vue')) {
         content = content.replace(/max-width:\s*800px;/g, 'max-width: 1200px;\n  width: 90%;');
         changed = true;
      }
    }
    
    // Fix avatar upload preview logic dynamically injecting image interceptor prefix immediately
    if (filePath.includes('merchant/Profile.vue')) {
        content = content.replace(/merchantInfo\.shangjiaPhoto = imagePath/g, "merchantInfo.shangjiaPhoto = imagePath.startsWith('/') ? imagePath : '/ershouchejiaoyi/upload/' + imagePath");
        changed = true;
    }
    if (filePath.includes('center/Profile.vue')) {
        content = content.replace(/profileForm\.value\.yonghuPhoto = response\.file \|\| response\.data/g, "const p = response.file || response.data;\n    profileForm.value.yonghuPhoto = p.startsWith('/') ? p : '/ershouchejiaoyi/upload/' + p");
        changed = true;
    }

    if (changed) {
      fs.writeFileSync(filePath, content);
      console.log('Updated: ' + filePath);
    }
  }
});
console.log('Widening CSS definitions completely executed across workspace.');
