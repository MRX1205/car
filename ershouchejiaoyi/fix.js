const fs = require('fs');
const path = require('path');

const replaceInFile = (file, regex, replacement) => {
    if (!fs.existsSync(file)) return;
    let content = fs.readFileSync(file, 'utf8');
    content = content.replace(regex, replacement);
    fs.writeFileSync(file, content);
};

// 1. Fix baseUrl issue for images
const imgFiles = [
    'front/src/views/News.vue',
    'front/src/views/NewsDetail.vue',
    'front/src/views/Forum.vue',
    'front/src/views/ForumDetail.vue',
    'front/src/views/merchant/Profile.vue',
    'front/src/layout/MerchantLayout.vue'
];
for (const file of imgFiles) {
    if (fs.existsSync(file)) {
        let content = fs.readFileSync(file, 'utf8');
        content = content.replace(/if\s*\([\w.]+\.startsWith\('http'\)\)\s*return\s*[\w.]+/g, (match) => {
            const varName = match.match(/if\s*\((.*?)\./)[1];
            return `if (${varName}.startsWith('http') || ${varName}.startsWith('/')) return ${varName}.split(',')[0]`;
        });
        fs.writeFileSync(file, content);
    }
}

// 2. Fix avatar saving response (`response.data` to `response.file` or handle properly)
const avatarRegex1 = /const imagePath = response\.data \|\| response\.filename \|\| response\.url/g;
replaceInFile('front/src/views/merchant/Profile.vue', avatarRegex1, 'const imagePath = response.file || response.data || response.filename || response.url');

const avatarRegex2 = /profileForm\.value\.yonghuPhoto = response\.data/g;
replaceInFile('front/src/views/center/Profile.vue', avatarRegex2, 'profileForm.value.yonghuPhoto = response.file || response.data');

// 3. Fix "万" suffix
replaceInFile('front/src/views/Cars.vue', /\{\{\s*car\.cheliangNewMoney\s*\}\}/g, '{{ car.cheliangNewMoney }} 万');
replaceInFile('front/src/views/CarDetail.vue', /¥\{\{\s*car\.cheliangNewMoney\s*\}\}/g, '¥{{ car.cheliangNewMoney }} 万');
replaceInFile('front/src/views/merchant/Vehicles.vue', /¥\{\{\s*row\.cheliangNewMoney\s*\}\}/g, '¥{{ row.cheliangNewMoney }} 万');
replaceInFile('front/src/views/center/Collections.vue', /\{\{\s*item\.cheliangNewMoney\s*\}\}/g, '{{ item.cheliangNewMoney }} 万');
replaceInFile('front/src/views/center/Cart.vue', /¥\{\{\s*item\.cheliangNewMoney\s*\}\}/g, '¥{{ item.cheliangNewMoney }} 万');
replaceInFile('front/src/views/merchant/Orders.vue', /¥\{\{\s*order\.cheliangOrderPrice\s*\}\}/g, '¥{{ order.cheliangOrderPrice }} 万');
replaceInFile('front/src/views/merchant/Orders.vue', /¥\{\{\s*currentOrder\.cheliangOrderPrice\s*\}\}/g, '¥{{ currentOrder.cheliangOrderPrice }} 万');
replaceInFile('front/src/views/center/Orders.vue', /¥\{\{\s*order\.cheliangOrderPrice\s*\}\}/g, '¥{{ order.cheliangOrderPrice }} 万');
replaceInFile('front/src/views/center/Orders.vue', /¥\{\{\s*currentOrder\.cheliangOrderPrice\s*\}\}/g, '¥{{ currentOrder.cheliangOrderPrice }} 万');
replaceInFile('front/src/views/Pay.vue', /¥\{\{\s*totalAmount\s*\}\}/g, '¥{{ totalAmount }} 万');
replaceInFile('front/src/views/merchant/Finance.vue', /¥\{\{\s*row\.cheliangOrderPrice\s*\}\}/g, '¥{{ row.cheliangOrderPrice }} 万');

console.log('All replacements complete.');
